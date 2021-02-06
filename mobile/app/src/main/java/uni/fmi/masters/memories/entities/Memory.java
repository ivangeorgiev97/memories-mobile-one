package uni.fmi.masters.memories.entities;

public class Memory {
    private int id;
    private String title;
    private String description;
    private int categoryId;
    private Category category;

    public Memory() {};

    public Memory(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Memory(int id, String title, String description, int categoryId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
    }

    public Memory(int id, String title, String description, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public Memory(int id, String title, String description, int categoryId, Category category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
