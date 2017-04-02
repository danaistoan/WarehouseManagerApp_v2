package com.tgs.warehouse.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="product_pallet")
public class ProductPallet {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="description")
	private String description;
	
	private List<ProductPackage> packages = new ArrayList<ProductPackage>();
	
	public ProductPallet(){
	}
	
	public ProductPallet(Long id, String description){
		this.id = id;
		this.description = description;
	}
	
	public ProductPallet(String description, List<ProductPackage> packages){
		this.description = description;
		this.packages = packages;
	}

	public void setId(Long id) {
		this.id = id;		
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProductPackage> getPackages() {
		return packages;
	}

	public void setPackages(List<ProductPackage> packages) {
		this.packages = packages;
	}	
	
	
}
