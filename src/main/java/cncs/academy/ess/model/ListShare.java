package cncs.academy.ess.model;

public class ListShare {
    private int listId;
    private int userId;
    private String permission;

    public ListShare(int listId, int userId, String permission){
        this.listId = listId;
        this.userId = userId;
        this.permission = permission;
    }

    public int getListId() { return listId; }
    public int getUserId() { return userId; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}
