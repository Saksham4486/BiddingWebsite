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

    abstract String displayItemDetails();
    abstract double calculateFinalPrice();

    boolean isAuctionActive() {
        return status.equals("active") && LocalDateTime.now().isBefore(auctionEndTime);
    }

    void extendAuction() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auctionEndTime)) {
            long minutesLeft = Duration.between(now, auctionEndTime).toMinutes();
            if (minutesLeft <= 5) {
                this.auctionEndTime = this.auctionEndTime.plusMinutes(5);
                System.out.println("Auction extended by 5 minutes for item: " + title);
            }
        }
    }

    String generateItemCode() {
        return "GEU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

class AuctionItem extends Item {
    String highestBidderId;

    public AuctionItem(String title, String description, String image, String category, int startingPrice, int minBidIncrement) {
        super(title, description, image, category, startingPrice, startingPrice, minBidIncrement, "active");
        this.highestBidderId = null;
    }

    @Override
    public String displayItemDetails() {
        return "ID: " + itemId +
               "\nTitle: " + title +
               "\nCategory: " + category +
               "\nDescription: " + description +
               "\nStarting Price: Rs." + startingPrice +
               "\nCurrent Price: Rs." + currentPrice +
               "\nMin Bid Increment: Rs." + minBidIncrement +
               "\nAuction Ends: " + auctionEndTime +
               "\nStatus: " + status;
    }

    @Override
    public double calculateFinalPrice() {
        return currentPrice;
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
    public Student(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Student");
    }

    @Override
    public void dashboard() {
        System.out.println("Welcome to the Student dashboard, " + name + "!");
        System.out.println("You can add items, place bids, view history and generate reports.");
    }
}

class Teacher extends User {
    public Teacher(String userId, String name, String email, String password) {
        super(userId, name, email, password, "Teacher");
    }

    @Override
    public void dashboard() {
        System.out.println("Welcome to the Teacher dashboard, " + name + "!");
        System.out.println("You can add items, place bids, view history and generate reports.");
    }
}

class NonTeachingStaff extends User {
    public NonTeachingStaff(String userId, String name, String email, String password) {
        super(userId, name, email, password, "NonTeachingStaff");
    }

    @Override
    public void dashboard() {
        System.out.println("Welcome to the Staff dashboard, " + name + "!");
        System.out.println("You can add items, place bids, view history and generate reports.");
    }
}

class Bid {
    String bidId;
    String bidderId;
    String itemId;
    int bidAmount;
    LocalDateTime bidTime;

    public Bid(String bidderId, String itemId, int bidAmount) {
        this.bidId = "BID-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.bidderId = bidderId;
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.bidTime = LocalDateTime.now();
    }
}

public class AuctionManager {
    private List<Item> itemList = new ArrayList<>();
    private List<Bid> allBids = new ArrayList<>();

    void addItem(Item item) {
        itemList.add(item);
        System.out.println("Item added successfully with code: " + item.itemId);
    }

    Item findItemById(String id) {
        for (Item item : itemList) {
            if (item.itemId.equals(id)) {
                return item;
            }
        }
        System.out.println("Item with ID " + id + " not found.");
        return null;
    }

    void placeBid(String itemId, User user, double amount) {
        Item item = findItemById(itemId);
        if (item == null) return;

        if (!item.isAuctionActive()) {
            System.out.println("Auction is not active for item: " + item.title);
            item.status = "closed";
            return;
        }

        int minRequired = (item.currentPrice == 0)
                ? item.startingPrice
                : item.currentPrice + item.minBidIncrement;

        if (amount < minRequired) {
            System.out.println("Bid too low. Minimum required: Rs." + minRequired);
            return;
        }

        item.currentPrice = (int) amount;
        if (item instanceof AuctionItem) {
            ((AuctionItem) item).highestBidderId = user.userId;
        }

        Bid newBid = new Bid(user.userId, item.itemId, (int) amount);
        allBids.add(newBid);
        item.extendAuction();
        System.out.println("Bid placed by " + user.name + " — Rs." + amount + " on " + item.title);
    }

    void closeAuction(String itemId) {
        Item item = findItemById(itemId);
        if (item == null) return;

        if (!item.isAuctionActive()) {
            System.out.println("Auction already closed for: " + item.title);
            return;
        }

        item.status = "closed";
        System.out.println("Auction closed for: " + item.title);

        if (item instanceof AuctionItem) {
            String winner = ((AuctionItem) item).highestBidderId;
            if (winner != null) {
                System.out.println("Winner (User ID): " + winner + " with final price: Rs." + item.calculateFinalPrice());
            } else {
                System.out.println("No bids placed. Item unsold.");
            }
        }
    }

    void listAllItems() {
        if (itemList.isEmpty()) {
            System.out.println("No items available.");
            return;
        }
        System.out.println("\n===== All Auction Items =====");
        for (Item item : itemList) {
            System.out.println(item.displayItemDetails());
            System.out.println("-----------------------------");
        }
    }

    List<Item> searchItems(String keyword, String category, String sortBy) {
        List<Item> result = new ArrayList<>();
        for (Item item : itemList) {
            boolean matchKeyword = keyword == null || item.title.toLowerCase().contains(keyword.toLowerCase())
                    || item.description.toLowerCase().contains(keyword.toLowerCase());
            boolean matchCategory = category == null || item.category.equalsIgnoreCase(category);
            if (matchKeyword && matchCategory) result.add(item);
        }

        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "price":
                    result.sort(Comparator.comparingInt(i -> i.currentPrice));
                    break;
                case "enddate":
                    result.sort(Comparator.comparing(i -> i.auctionEndTime));
                    break;
                case "title":
                    result.sort(Comparator.comparing(i -> i.title));
                    break;
            }
        }
        return result;
    }

    void showBidHistory(String itemId) {
        Item item = findItemById(itemId);
        if (item == null) return;
        System.out.println("\nBid History for: " + item.title);
        boolean found = false;
        for (Bid bid : allBids) {
            if (bid.itemId.equals(itemId)) {
                System.out.println("Bid ID: " + bid.bidId + " | Bidder: " + bid.bidderId + " | Amount: Rs." + bid.bidAmount + " | Time: " + bid.bidTime);
                found = true;
            }
        }
        if (!found) System.out.println("No bids placed yet.");
    }

    public static void main(String[] args) {
    }
}