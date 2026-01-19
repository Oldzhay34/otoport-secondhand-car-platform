package com.example.otoportdeneme.dto_Objects;

public class ListingImageDto {
    private Long id;
    private String imagePath;   // uploads/....
    private Integer sortOrder;
    private Boolean cover;

    public ListingImageDto() {}

    public ListingImageDto(Long id, String imagePath, Integer sortOrder, Boolean cover) {
        this.id = id;
        this.imagePath = imagePath;
        this.sortOrder = sortOrder;
        this.cover = cover;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getCover() { return cover; }
    public void setCover(Boolean cover) { this.cover = cover; }
}
