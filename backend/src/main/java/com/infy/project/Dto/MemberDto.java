package com.infy.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String major;
    private String role;   // Admin / Member
	public MemberDto(Long id, String name, String major, String role) {
		super();
		this.id = id;
		this.name = name;
		this.major = major;
		this.role = role;
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
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
    
    
}