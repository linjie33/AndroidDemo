package com.planetwalk.ponion.db.Entity;

public class BuddyConstants {
    // 服务器的关系可以是
    //
    // Friend(1)
    // Delete(3)
    // BeDeleted(4)
    // Block(5)
    // BeBlocked(6)
    // FriendRequest(7)
    // FriendPendingApproval(8)
    // Reject(9)
    // BeRejected(10)
    // IKnow(11)
    // KnowMe(12)

    public static final int TYPE_FRIEND = 1;
    public static final int TYPE_ME = 2; // 我
    public static final int TYPE_STRANGER = 5; // 陌生人，服务器不会存储这种关系，但是客户端需要有这个关系来表示两个人之间的默认关系。

    // IKnow(11)
    public static final int TYPE_I_KNOW = 6; // 我有对方的联系方式

    // KnowMe(12)
    public static final int TYPE_KNOW_ME = 7; // 对方有我的联系方式

    public static final int TYPE_GROUP = 8; // 群聊buddy
    public static final int TYPE_GROUP_MEMBER = 9; // 群聊成员buddy

    // Block(5)
    public static final int TYPE_BLOCK = 14; // 在黑名单中的好友。

    // BeBlocked(6)
    public static final int TYPE_BE_BLOCKED = 15; // 被对方拉入了黑名单。

    // FriendRequest(7)
    public static final int TYPE_ADDED_ME = 16; // 添加我的人（好友请求）

    // FriendPendingApproval(8)
    public static final int TYPE_I_ADDED = 17; // 我添加的人（请求好友）

    public static final int TYPE_OFFICEAL_ROBOT = 114; // 官方小助手

    public static class PetStatus{
        public static final int HOME = 0;
        public static final int TRAVEL = 1;
        public static final int HOMELESS = 2;
    }
}
