public class User {

    private int userId;
    private int roleId;
    private String name;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String status;

    public User() {
    }

    public User(int roleId,
                String name,
                String username,
                String email,
                String password,
                String phone) {

        this.roleId = roleId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;

    }

    public User(int userId,
                int roleId,
                String name,
                String username,
                String email,
                String password,
                String phone,
                String status) {

        this.userId = userId;
        this.roleId = roleId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.status = status;

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}