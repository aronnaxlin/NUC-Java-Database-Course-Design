package site.aronnax.entity;

/**
 * 房产实体类
 * 对应数据库 properties 表
 *
 * @author Aronnax (Li Linhan)
 * @version 1.0
 */
public class Property {

    private Long pId; // 主键ID
    private String buildingNo; // 楼栋号
    private String unitNo; // 单元号
    private String roomNo; // 房间号
    private Double area; // 建筑面积
    private String pStatus; // 房屋状态: SOLD, UNSOLD, RENTED
    private Long userId; // 关联用户ID

    // 无参构造器
    public Property() {
    }

    // 全参构造器
    public Property(Long pId, String buildingNo, String unitNo, String roomNo,
            Double area, String pStatus, Long userId) {
        this.pId = pId;
        this.buildingNo = buildingNo;
        this.unitNo = unitNo;
        this.roomNo = roomNo;
        this.area = area;
        this.pStatus = pStatus;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getpStatus() {
        return pStatus;
    }

    public void setpStatus(String pStatus) {
        this.pStatus = pStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Property{" +
                "pId=" + pId +
                ", buildingNo='" + buildingNo + '\'' +
                ", unitNo='" + unitNo + '\'' +
                ", roomNo='" + roomNo + '\'' +
                ", area=" + area +
                ", pStatus='" + pStatus + '\'' +
                ", userId=" + userId +
                '}';
    }
}
