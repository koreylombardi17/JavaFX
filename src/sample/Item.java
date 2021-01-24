package sample;

public class Item {

    private String id;
    private String name;
    private Boolean inStock;
    private float price;
    private String details;

    public Item(String id, String name, Boolean inStock, float price) {
        this.id = id;
        this.name = name;
        this.inStock = inStock;
        this.price = price;
        this.details = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", inStock=" + inStock +
                ", price=" + price +
                '}';
    }
}
