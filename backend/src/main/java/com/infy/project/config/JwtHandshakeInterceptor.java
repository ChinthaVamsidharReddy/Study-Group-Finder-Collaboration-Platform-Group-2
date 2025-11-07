//com/infy/project/config/JwtHandshakeInterceptor.java
package com.infy.project.config;



import com.infy.project.security.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

 private final JwtUtil jwtUtil = new JwtUtil();

 @Override
 public boolean beforeHandshake(
         ServerHttpRequest request,
         ServerHttpResponse response,
         WebSocketHandler wsHandler,
         Map<String, Object> attributes) {

     String query = request.getURI().getQuery();
     if (query != null && query.contains("token=")) {
         String token = query.split("token=")[1];
         if (jwtUtil.validateToken(token)) {
             String email = jwtUtil.extractUsername(token);
             attributes.put("userEmail", email);
             System.out.println("✅ STOMP connection received with token: " + token);
             return true;
         }
     }
     System.out.println("❌ Invalid or missing token during STOMP handshake.");
     return false;
 }

 @Override
 public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Exception exception) {}
}
