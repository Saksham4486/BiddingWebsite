import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
abstract class Item {
    String itemId;
    String title;
    String description;
    String image;
    String category;
    int startingPrice = 0;
    int currentPrice;
    int minBidIncrement = 5;
    LocalDateTime auctionEndTime;
    String status;
    public Item(String title, String description, String image, String category, int startingPrice, int currentPrice, int minBidIncrement, String status) {
        this.itemId = generateItemCode();
        this.title = title;
        this.description = description;
        this.image = image;
        this.category = category;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.minBidIncrement = minBidIncrement;
        this.auctionEndTime = LocalDateTime.now().plusDays(3);
        this.status = status;
    }
    abstract void placeBid();
    abstract void closeAuction();
    abstract String displayItemDetails();
    abstract double calculateFinalPrice();

    boolean isAuctionActive() {
        return status.equals("active");
    }
    void extendAuction() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auctionEndTime)) {
            long minutesLeft = Duration.between(now, auctionEndTime).toMinutes();
            if (minutesLeft <= 5) {
                this.auctionEndTime = this.auctionEndTime.plusMinutes(5);
            }
        }
    }
    String generateItemCode() {
        StringBuilder uniquecode = new StringBuilder("GEU-");
        uniquecode.append(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return uniquecode.toString();
    }
}
abstract class User {
    String userId;
    String name;
    String email;
    String password;
    String role;
    public User(String userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    abstract void dashboard();
}
class Student extends User {
    public Student(String userId, String name, String email, String password, String role) {
        super(userId, name, email, password, role);
    }
    @Override
    void dashboard() {
        System.out.println("Welcome to the Student dashboard, " + name + "!");
    }
}
class Teacher extends User {
    public Teacher(String userId, String name, String email, String password, String role) {
        super(userId, name, email, password, role);
    }
    @Override
    void dashboard() {
        System.out.println("Welcome to the Teacher dashboard, " + name + "!");
    }
}
class NonTeachingStaff extends User {
    public NonTeachingStaff(String userId, String name, String email, String password, String role) {
        super(userId, name, email, password, role);
    }
    @Override
    void dashboard() {
        System.out.println("Welcome to the Staff dashboard, " + name + "!");
    }
}
class Bid {
    String bidId;
    String bidderId;
    String itemId;
    int bidAmount;
    String bidTime;
    public Bid(String bidId, String bidderId, String itemId, int bidAmount, String bidTime) {
        this.bidId = bidId;
        this.bidderId = bidderId;
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }
}
public class AuctionManager {
    private List<Item> itemList = new ArrayList<>();
    void addItem(Item item) {
        itemList.add(item);
        System.out.println("Item added successfully with code: " + item.itemId);
    }
    void findItembyId(String id) {
        System.out.println("Searching for item with ID: " + id);
        for (Item item : itemList) {
            if (item.itemId.equals(id)) {
                System.out.println("Item found: " + item.title);
                return;
            }
        }
        System.out.println("Item not found.");
    }
}