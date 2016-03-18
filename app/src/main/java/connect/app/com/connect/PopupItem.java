package connect.app.com.connect;

/**
 * Created by 44260 on 2016/3/16.
 */
public class PopupItem {
    private int id;
    private boolean disable;
    private String name;

    public int getId() {
        return id;
    }

    public PopupItem setId(int id) {
        this.id = id;
        return this;
    }

    public boolean isDisable() {
        return disable;
    }

    public PopupItem setDisable(boolean disable) {
        this.disable = disable;
        return this;
    }

    public String getName() {
        return name;
    }

    public PopupItem setName(String name) {
        this.name = name;
        return this;
    }

}
