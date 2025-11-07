package com.infy.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class AdminDto{
    
	private Long id;
    private String name;
    private String major;
	public AdminDto(Long id, String name, String major) {
		super();
		this.id = id;
		this.name = name;
		this.major = major;
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
    
    
}
