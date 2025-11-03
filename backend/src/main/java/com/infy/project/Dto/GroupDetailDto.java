package com.infy.project.Dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class GroupDetailDto{
    private Long id;
    private String name;
    private String description;
    private String courseId;
    private AdminDto createdBy;          // Admin info
    private List<MemberDto> members;     // All members
	public GroupDetailDto(Long id, String name, String description, String courseId, AdminDto createdBy,
			List<MemberDto> members) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.courseId = courseId;
		this.createdBy = createdBy;
		this.members = members;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public AdminDto getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(AdminDto createdBy) {
		this.createdBy = createdBy;
	}
	public List<MemberDto> getMembers() {
		return members;
	}
	public void setMembers(List<MemberDto> members) {
		this.members = members;
	}
    
    
    
}