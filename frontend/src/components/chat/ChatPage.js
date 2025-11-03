import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useChat } from '../../contexts/ChatContext';
import { useAuth } from '../../contexts/AuthContext';
import { ArrowLeftIcon, EllipsisVerticalIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import UserPresence from './UserPresence';
import MemberList from './MemberList';
import PinnedMessages from './PinnedMessages';
import GroupSettings from './GroupSettings';
import MessageInput from './MessageInput';
import MessageBubble from './MessageBubble';
import TypingIndicator from './TypingIndicator';
import PollDisplay from "./PollDisplay";


const API_BASE = "http://localhost:8080/api";
const token = localStorage.getItem("token");

const ChatPage = () => {
  const { groupId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex items-center justify-center h-screen text-gray-500">
        Loading chat...
      </div>
    );
  }

const {
  activeGroup,
  openGroup,
  closeGroup,
  getGroupMessages,
  sendMessage,
  sendTypingIndicator,
  sendTypingStopIndicator, 
  getTypingUsers,
  setActiveGroup,
  markAsRead,
  addReaction,
  votePoll,
  sendPoll
} = useChat();


  const [replyTo, setReplyTo] = useState(null);
  const [groupInfo, setGroupInfo] = useState(null);
  const [oldMessages, setOldMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const messagesEndRef = useRef(null);
  const observerRef = useRef(null);
  const observedIdsRef = useRef(new Set());
  const [showMenu, setShowMenu] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState(null);



// const messages = getGroupMessages(activeGroup);
// const typingUsers = getTypingUsers(activeGroup);

const messages = getGroupMessages(activeGroup) || [];
const typingUsers = (getTypingUsers(activeGroup) || []).filter(
  (u) => u && String(u.id) !== String(user?.id)
);



  // ✅ Fetch group info & messages + setup WebSocket subscription
  useEffect(() => {
    if (!groupId) return;

    setActiveGroup(groupId);
    markAsRead(groupId);
    fetchGroupDetails(groupId);
    
    // ✅ Fetch messages first, then polls (to ensure proper merging)
    const loadData = async () => {
      await fetchOldMessages(groupId);
      // Wait a bit for messages to be set, then fetch polls
      setTimeout(() => {
        fetchGroupPolls(groupId);
      }, 100);
    };
    loadData();
    
    openGroup(groupId); // subscribe to STOMP topic

    // ✅ Listen for poll vote updates from WebSocket
    const handlePollVoteUpdate = (e) => {
      const { poll } = e.detail || {};
      if (!poll || !poll.id) return;

      setOldMessages((prev) =>
        prev.map((msg) => {
          if (msg.type === "poll" && String(msg.poll?.id) === String(poll.id)) {
            return {
              ...msg,
              poll: {
                ...msg.poll,
                ...poll,
                options: poll.options || msg.poll.options,
                totalVotes: poll.totalVotes !== undefined ? poll.totalVotes : msg.poll.totalVotes,
              },
            };
          }
          return msg;
        })
      );
    };

    // ✅ Listen for new poll creation from WebSocket
    const handlePollCreated = (e) => {
      const { pollMessage } = e.detail || {};
      if (!pollMessage || !pollMessage.poll?.id) return;

      setOldMessages((prev) => {
        // Check if poll already exists
        const exists = prev.some(
          (m) =>
            m.type === "poll" && String(m.poll?.id) === String(pollMessage.poll.id)
        );
        if (exists) {
          // Update existing poll
          return prev.map((m) =>
            m.type === "poll" && String(m.poll?.id) === String(pollMessage.poll.id)
              ? pollMessage
              : m
          );
        }
        // Add new poll
        return [...prev, pollMessage];
      });
    };

    window.addEventListener("poll:voteUpdate", handlePollVoteUpdate);
    window.addEventListener("poll:created", handlePollCreated);

    return () => {
      closeGroup(groupId); // unsubscribe when switching groups
      window.removeEventListener("poll:voteUpdate", handlePollVoteUpdate);
      window.removeEventListener("poll:created", handlePollCreated);
    };
  }, [groupId]);

  // ✅ IntersectionObserver to detect visible messages and send read acks once
  useEffect(() => {
    if (!groupId) return;
    const currentUserId = user?.id || localStorage.getItem("userId");
    if (observerRef.current) observerRef.current.disconnect();
    observerRef.current = new IntersectionObserver(
      (entries) => {
        const idsToAck = [];
        const mergedList = [...oldMessages, ...(messages || [])];
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const el = entry.target;
            const id = el.getAttribute('data-message-id');
            if (id && !observedIdsRef.current.has(id)) {
              const msg = mergedList.find((m) => String(m.id) === String(id));
              // ✅ Skip poll messages (they have string IDs like "poll-9") 
              // Only send read receipts for regular messages with numeric IDs
              if (msg && 
                  String(msg.senderId) !== String(currentUserId) && 
                  msg.type !== "poll") { // Polls don't need read receipts
                
                // ✅ Only send numeric message IDs (Long) to backend
                // Convert string ID to number if it's numeric, or skip if it's not
                const numericId = typeof id === 'string' && id.match(/^\d+$/) 
                  ? parseInt(id, 10) 
                  : (typeof id === 'number' ? id : null);
                
                if (numericId !== null && !isNaN(numericId)) {
                  observedIdsRef.current.add(id);
                  idsToAck.push(numericId);
                } else {
                  // Mark as observed even if we don't ack (to avoid retrying)
                  observedIdsRef.current.add(id);
                }
              }
            }
          }
        });
        if (idsToAck.length > 0) {
          try {
            const evt = new CustomEvent("chat:readReceipt", { detail: { groupId, messageIds: idsToAck } });
            window.dispatchEvent(evt);
          } catch (e) {
            console.error("Error dispatching read receipt event:", e);
          }
        }
      },
      { root: null, rootMargin: '0px', threshold: 0.6 }
    );

    // Observe all message bubbles with data-message-id (excluding polls and non-numeric IDs)
    setTimeout(() => {
      document.querySelectorAll('[data-message-id]')?.forEach((el) => {
        const id = el.getAttribute('data-message-id');
        // ✅ Only observe elements with numeric IDs (polls have "poll-9" format)
        // This prevents trying to send read receipts for polls
        if (id && /^\d+$/.test(id)) {
          observerRef.current?.observe(el);
        }
      });
    }, 100);

    return () => {
      observerRef.current?.disconnect();
    };
  }, [groupId, oldMessages, messages]);

  // ✅ Fetch group info
  const fetchGroupDetails = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/groups/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) {
        const data = await res.json();
        setGroupInfo(data);
      } else {
        console.error('Failed to fetch group details');
      }
    } catch (err) {
      console.error('Error fetching group details:', err);
    }
  };

  // ✅ Fetch old messages
  const fetchOldMessages = async (id) => {
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/chat/messages/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (res.ok) {
        let data = await res.json();

      // ✅ Normalize poll messages from REST API
      data = data.map((m) => {
        if (m.type === "poll" || m.pollQuestion) {
          // If poll data is already in the correct format, use it
          if (m.poll) {
            return {
              ...m,
              type: "poll",
              timestamp: m.timestamp || m.createdAt || m.poll.createdAt,
            };
          }
          
          // Otherwise, normalize from old format
          return {
            id: m.id,
            type: "poll",
            poll: {
              id: m.pollId || m.id,
              question: m.pollQuestion || m.content,
              options: m.pollOptions || m.options || [],
              allowMultiple: m.allowMultiple || false,
              anonymous: m.anonymous || false,
              createdAt: m.createdAt,
              creatorId: m.creatorId || m.createdBy,
              creatorName: m.creatorName,
            },
            senderId: m.senderId,
            senderName: m.senderName,
            timestamp: m.createdAt || m.timestamp,
            groupId: m.groupId,
          };
        }
        
        // ✅ Normalize file messages - ensure content is always a string
        if (m.type === "file") {
          return {
            ...m,
            type: "file",
            content: typeof m.content === 'string' ? m.content : (m.content?.name || m.content?.fileName || "File"),
            fileUrl: m.fileUrl || m.url,
            fileType: m.fileType, // MIME type (e.g., "image/png", "application/pdf")
            size: m.size || m.fileSize,
            timestamp: m.timestamp || m.createdAt,
          };
        }
        
        // ✅ Ensure content is always a string for text messages
        if (m.type !== "poll" && m.type !== "file" && typeof m.content !== 'string') {
          return {
            ...m,
            content: typeof m.content === 'string' ? m.content : String(m.content || ''),
            timestamp: m.timestamp || m.createdAt,
          };
        }
        
        return {
          ...m,
          timestamp: m.timestamp || m.createdAt,
        };
      });

      // compute initial status for own messages if delivered/read arrays provided
      const currentUserId = user?.id || localStorage.getItem("userId");
      const withStatus = (data || []).map((m) => {
        if (String(m.senderId) !== String(currentUserId)) return m;
        const total = Number.isInteger(m.totalRecipients) ? m.totalRecipients : undefined;
        const delivered = Array.isArray(m.deliveredBy) ? m.deliveredBy.length : 0;
        const read = Array.isArray(m.readBy) ? m.readBy.length : 0;
        if (total != null) {
          if (read >= total) return { ...m, status: 'read' };
          if (delivered >= total) return { ...m, status: 'delivered' };
          return { ...m, status: 'sent' };
        }
        return m;
      });

      setOldMessages(withStatus); 
      } else {
        console.error("Failed to fetch old messages:", res.status);
      }
    } catch (err) {
      console.error("Error fetching old messages:", err);
    } finally {
      setLoading(false);
    }
  };


  // ✅ Fetch existing polls for this group
const fetchGroupPolls = async (id) => {
  try {
    const res = await fetch(`http://localhost:8080/polls/group/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (res.ok) {
      const polls = await res.json();
      console.log(`📊 Fetched ${polls?.length || 0} polls for group ${id}`, polls);
      
      if (!Array.isArray(polls) || polls.length === 0) {
        console.log(`No polls found for group ${id}`);
        return; // No polls to add
      }

      const formattedPolls = polls.map((poll) => ({
        id: `poll-${poll.id}`,
        type: "poll",
        poll: {
          id: poll.id,
          question: poll.question,
          options: poll.options || [],
          allowMultiple: poll.allowMultiple || false,
          anonymous: poll.anonymous || false,
          totalVotes: poll.totalVotes || 0,
          createdAt: poll.createdAt,
          creatorId: poll.creatorId || poll.createdBy,
          creatorName: poll.creatorName,
        },
        senderId: poll.creatorId || poll.createdBy || null,
        senderName: poll.creatorName || "Unknown",
        timestamp: poll.createdAt || new Date().toISOString(),
        groupId: id,
      }));

      // ✅ Merge polls & deduplicate properly - preserve existing messages
      setOldMessages((prev) => {
        const merged = [...prev];
        
        formattedPolls.forEach((pollMsg) => {
          const pollId = pollMsg.poll?.id;
          if (!pollId) return;
          
          // Check if poll already exists by poll.id
          const existingIndex = merged.findIndex(
            (m) =>
              m.type === "poll" &&
              String(m.poll?.id) === String(pollId)
          );
          
          if (existingIndex !== -1) {
            // Update existing poll with latest data (might have vote updates)
            merged[existingIndex] = pollMsg;
          } else {
            // Add new poll if it doesn't exist
            merged.push(pollMsg);
          }
        });
        
        return merged;
      });
    } else {
      console.error("Failed to fetch group polls:", res.status, res.statusText);
    }
  } catch (err) {
    console.error("Error fetching group polls:", err);
  }
};

// ChatPage.js — handleFileUpload
const handleFileUpload = async (messageData) => {
  if (!groupId) {
    console.warn("No group ID found in route for file upload");
    return;
  }

  try {
    const file = messageData.file.file; // from your FileUpload component
    const formData = new FormData();
    formData.append("file", file);
    formData.append("groupId", groupId);
    formData.append("senderId", localStorage.getItem("userId"));
    formData.append("senderName",localStorage.getItem("name"));

    const res = await fetch(`${API_BASE}/files/upload`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` }, // don't set Content-Type, browser will set multipart
      body: formData,
    });

    if (!res.ok) {
      const txt = await res.text().catch(() => null);
      throw new Error(`Upload failed: ${res.status} ${res.statusText} ${txt || ""}`);
    }

    const data = await res.json();
    console.log("File uploaded successfully:", data);

    // IMPORTANT: DON'T call sendMessage(...) with an object payload here.
    // The backend already broadcasts the file message to /topic/group.{groupId}.
    // Option: optimistic local hint (optional), but avoid creating a message with content object.

    // Example optional lightweight optimistic message (string content only):
    // addLocalTemporaryMessage({
    //   groupId,
    //   senderId: user.id,
    //   senderName: user.name,
    //   content: data.fileName || file.name,
    //   fileUrl: data.fileUrl, fileType: data.fileType, size: data.size,
    //   timestamp: new Date().toISOString(),
    //   type: 'file',
    //   localOnly: true
    // });

  } catch (err) {
    console.error("❌ File upload error:", err);
  }
};





  // ✅ Auto-scroll to bottom when new messages appear
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, oldMessages]);

  // ✅ Send message logic — no local duplication now
  const handleSendMessage = (messageData) => {
    if (!messageData.content?.trim()) return;
    sendMessage(groupId, messageData.content);
  };

  const handleReaction = (messageId, emoji) => {
    addReaction(groupId, messageId, emoji);
  };

  const handleReply = (message) => {
    setReplyTo({
      id: message.id,
      content: message.content,
      senderName: message.senderName
    });
  };

  // const handlePollVote = (pollId, optionIds) => {
  //   const pollMessage = messages.find(msg => msg.poll && msg.poll.id === pollId);
  //   if (pollMessage) {
  //     votePoll(groupId, pollMessage.id, pollId, optionIds);
  //   }
  // };

  const handlePollVote = (pollId, optionIds) => {
    const userId = user?.id || localStorage.getItem("userId");
    const userIdLong = userId ? (typeof userId === 'string' ? parseInt(userId, 10) : userId) : null;

    // ✅ Optimistic update - will be replaced by WebSocket update
    setOldMessages((prev) =>
      prev.map((msg) => {
        if (msg.poll && String(msg.poll.id) === String(pollId)) {
          const updatedOptions = msg.poll.options.map((opt) => {
            // Check if user already voted for this option
            const hasVoted = (opt.votes || []).some(v => {
              const voteId = typeof v === 'object' ? v.userId : v;
              return String(voteId) === String(userId);
            });
            
            if (optionIds.includes(opt.id) && !hasVoted) {
              // Add user ID to votes (backend expects List<Long>)
              return {
                ...opt,
                votes: [...(opt.votes || []), userIdLong || userId]
              };
            }
            return opt;
          });
          
          // Recalculate total votes
          const newTotalVotes = updatedOptions.reduce((sum, opt) => sum + (opt.votes?.length || 0), 0);
          
          return {
            ...msg,
            poll: {
              ...msg.poll,
              options: updatedOptions,
              totalVotes: newTotalVotes
            }
          };
        }
        return msg;
      })
    );

    // Also update in WebSocket messages array
    // The WebSocket response will override this, but this provides immediate feedback

    // ✅ Notify backend
    votePoll(groupId, null, pollId, optionIds);
  };


  // ✅ Merge and deduplicate messages before rendering
const combinedMessages = React.useMemo(() => {
  const merged = [...oldMessages, ...messages];
  const unique = [];
  const seenPollIds = new Set(); // Track poll IDs separately
  
  for (const msg of merged) {
    // For polls, use poll.id for deduplication; for other messages, use message id
    if (msg.type === "poll" && msg.poll?.id) {
      const pollId = String(msg.poll.id);
      if (seenPollIds.has(pollId)) {
        // Update existing poll with latest data (WebSocket messages are more recent)
        const existingIndex = unique.findIndex(
          (m) => m.type === "poll" && String(m.poll?.id) === pollId
        );
        if (existingIndex !== -1) {
          // Keep the one with more recent timestamp or more complete data
          const existing = unique[existingIndex];
          const existingTime = new Date(existing.timestamp || existing.createdAt || 0).getTime();
          const newTime = new Date(msg.timestamp || msg.createdAt || 0).getTime();
          if (newTime > existingTime || (msg.poll?.options?.length > existing.poll?.options?.length)) {
            unique[existingIndex] = msg;
          }
        }
        continue;
      }
      seenPollIds.add(pollId);
    } else {
      // Regular message deduplication by id
      if (unique.some((m) => m.id && msg.id && String(m.id) === String(msg.id))) {
        continue;
      }
    }
    unique.push(msg);
  }
  
  return unique.sort((a, b) => {
    const timeA = new Date(a.timestamp || a.createdAt || 0).getTime();
    const timeB = new Date(b.timestamp || b.createdAt || 0).getTime();
    return timeA - timeB;
  });
}, [oldMessages, messages]);


// ✅ Group messages by date (Today, Yesterday, or date string)
const groupMessagesByDate = (messages) => {
  const groups = {};

  messages.forEach((msg) => {
    const date = new Date(msg.timestamp || msg.createdAt);
    const today = new Date();

    let label;

    // Compare with today and yesterday
    const isToday =
      date.toDateString() === today.toDateString();

    const isYesterday =
      date.toDateString() ===
      new Date(today.setDate(today.getDate() - 1)).toDateString();

    if (isToday) label = "Today";
    else if (isYesterday) label = "Yesterday";
    else
      label = date.toLocaleDateString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric",
      });

    if (!groups[label]) groups[label] = [];
    groups[label].push(msg);
  });

  return groups;
};


  return (
    <div className="h-screen flex flex-col bg-gray-50 dark:bg-dark-bg">
      {/* Header */}
      <div className="bg-white dark:bg-dark-surface border-b px-3 sm:px-4 py-3 flex items-center justify-between gap-2 shadow-sm">
        <div className="flex items-center gap-3 flex-1">
          <button
            onClick={() => navigate('/dashboard')}
            className="p-2 hover:bg-gray-100 dark:hover:bg-dark-input rounded-lg transition"
          >
            <ArrowLeftIcon className="h-5 w-5 text-gray-600 dark:text-dark-text" />
          </button>

          <div className="flex flex-col min-w-0">
            <div className="flex items-center gap-2">
              <img
                src={`https://ui-avatars.com/api/?name=${groupInfo?.name}&background=random`}
                alt="Group Avatar"
                className="h-10 w-10 rounded-full"
              />
              <div className="min-w-0">
                <h1 className="text-base sm:text-lg font-semibold text-gray-900 dark:text-dark-text truncate">
                  {groupInfo?.name || 'Study Group'}
                </h1>
                <p className="text-xs text-gray-500 dark:text-dark-textSecondary">
                  {groupInfo?.coursename || 'General'}
                </p>
              </div>
            </div>

            <UserPresence
              groupId={groupId}
              groupName={groupInfo?.name || 'Study Group'}
              members={groupInfo?.members || []}
              
            />
          </div>
        </div>

        {/* Desktop actions */}
        <div className="hidden sm:flex items-center gap-1">
          <MemberList groupId={groupId} groupName={groupInfo?.name || 'Study Group'} members={groupInfo?.members || []} />
          <PinnedMessages groupId={groupId} />
          <GroupSettings groupId={groupId} groupName={groupInfo?.name || 'Study Group'} members={groupInfo?.members || []} />
        </div>

        {/* Mobile menu */}
        <div className="sm:hidden relative">
          <button
            onClick={() => setShowMenu((s) => !s)}
            className="p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-dark-input"
            aria-label="More options"
          >
            <EllipsisVerticalIcon className="h-6 w-6 text-gray-700 dark:text-dark-text" />
          </button>
          {showMenu && (
            <div className="absolute right-2 mt-2 w-48 bg-white dark:bg-dark-surface border border-gray-200 dark:border-dark-border rounded-lg shadow-lg z-20">
              <div className="border-t border-gray-100 dark:border-dark-border" />
              <div className="px-1 py-1">
              <button className="w-full flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-dark-input text-sm" onClick={() => setShowMenu(false)}>
                <MagnifyingGlassIcon className="h-4 w-4" /> Search
              </button>
              </div>
              <div className="border-t border-gray-100 dark:border-dark-border" />
              <div className="px-1 py-1">
              <button className="w-full flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-dark-input text-sm">
                <MemberList groupId={groupId} groupName={groupInfo?.name || 'Study Group'} members={groupInfo?.members || []} />Members List
              </button>
              </div>
              <div className="border-t border-gray-100 dark:border-dark-border" />
              <div className="px-1 py-1">
              <button className="w-full flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-dark-input text-sm" >
                <GroupSettings groupId={groupId} groupName={groupInfo?.name || 'Study Group'} members={groupInfo?.members || []} />Settings
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Messages Section */}
        <div className="flex-1 overflow-y-auto px-3 sm:px-4 py-3 sm:py-4 space-y-3 sm:space-y-4" style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}>
          <style>{`div[style*="scrollbar-width: none"]::-webkit-scrollbar{ display: none; }`}</style>
          {loading ? (
            <div className="h-full flex items-center justify-center text-gray-500">
              Loading messages...
            </div>
          ) : combinedMessages.length === 0 ? (
            <div className="h-full flex items-center justify-center text-gray-500">
              {/* No messages yet. Start the conversation! */}
            </div>
          ) : (
            <>
              {Object.entries(groupMessagesByDate(combinedMessages)).map(
  ([dateLabel, messagesForDate]) => (
    <div key={dateLabel}>
      {/* ✅ Date Header */}
      <div className="flex justify-center my-2">
        <span className="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 text-xs px-3 py-1 rounded-full">
          {dateLabel}
        </span>
      </div>

      {/* ✅ Messages under this date */}
      {messagesForDate.map((message) => {
        const currentUserId = user?.id || localStorage.getItem("userId");
        const isMine = String(message?.senderId) === String(currentUserId);

        // ✅ Poll message alignment - WhatsApp-like styling
        if (message.type === "poll" || message.poll) {
          const poll = message.poll || message;
          return (
            <div
              key={message?.id || `poll-${poll.id || poll.question}`}
              data-message-id={message?.id}
              className={`flex w-full my-3 sm:my-4 ${
                isMine ? "justify-end" : "justify-start"
              }`}
            >
              <div
                className={`max-w-[85%] sm:max-w-[75%] md:max-w-[65%] ${
                  isMine ? "ml-auto" : ""
                }`}
              >
                <PollDisplay
                  poll={poll}
                  onVote={(pollId, optionIds) =>
                    handlePollVote(pollId, optionIds)
                  }
                  isOwn={isMine}
                  className="shadow-sm"
                />
              </div>
            </div>
          );
        }

        // ✅ Normal message
        return (
          <MessageBubble
            key={message?.id || message?.timestamp}
            message={message}
            isOwn={isMine}
            onReply={handleReply}
            onReaction={handleReaction}
            onPollVote={handlePollVote}
          />
        );
      })}
    </div>
  )
)}

{typingUsers.length > 0 && <TypingIndicator users={typingUsers} />}
<div ref={messagesEndRef} />

            </>
          )}
        </div>


      {/* Message Input */}
        <MessageInput
          onSendMessage={(msg) => sendMessage(activeGroup || groupId, msg.content)}
          onSendPoll={(poll) => sendPoll(activeGroup || groupId, poll)}  // ✅ ADD THIS
          onTyping={(isTyping) => {
            const gId = activeGroup || groupId;
            if (isTyping) sendTypingIndicator(gId);
            else sendTypingStopIndicator(gId);
          }}
          onSendFile={handleFileUpload}
          replyTo={replyTo}
          onCancelReply={() => setReplyTo(null)}
          placeholder="Type a message..."
        />
        {/* <PollDisplay poll={poll} onVote={(pollId, optionIds) => handlePollVote(pollId, optionIds)} /> */}

    </div>
    
  );
};

export default ChatPage;
