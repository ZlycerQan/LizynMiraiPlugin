package org.zlycerqan.mirai.lizyn.core.service;

import net.mamoe.mirai.event.events.*;

import java.util.function.Consumer;

public interface EventSolver {

    Consumer<FriendMessageEvent> solveFriendMessageEvent(FriendMessageEvent event);
    Consumer<GroupMessageEvent> solveGroupMessageEvent(GroupMessageEvent event);
    Consumer<BotGroupPermissionChangeEvent> solveBotGroupPermissionChangeEvent(BotGroupPermissionChangeEvent event);
    Consumer<BeforeImageUploadEvent> solveBeforeImageUploadEvent(BeforeImageUploadEvent event);
    Consumer<BotInvitedJoinGroupRequestEvent> solveBotInvitedJoinGroupRequestEvent(BotInvitedJoinGroupRequestEvent event);
    Consumer<BotJoinGroupEvent> solveBotJoinGroupEvent(BotJoinGroupEvent event);
    Consumer<BotLeaveEvent> solveBotLeaveEvent(BotLeaveEvent event);
    Consumer<BotMuteEvent> solveBotMuteEvent(BotMuteEvent event);
    Consumer<BotNickChangedEvent> solveBotNickChangedEvent(BotNickChangedEvent event);
    Consumer<BotAvatarChangedEvent> solveBotAvatarChangedEvent(BotAvatarChangedEvent event);
    Consumer<BotOfflineEvent> solveBotOfflineEvent(BotOfflineEvent event);
    Consumer<BotOnlineEvent> solveBotOnlineEvent(BotOnlineEvent event);
    Consumer<BotReloginEvent> solveBotReloginEvent(BotReloginEvent event);
    Consumer<BotUnmuteEvent> solveBotUnmuteEvent(BotUnmuteEvent event);
    Consumer<FriendAddEvent> solveFriendAddEvent(FriendAddEvent event);
    Consumer<FriendAvatarChangedEvent> solveFriendAvatarChangedEvent(FriendAvatarChangedEvent event);
    Consumer<FriendDeleteEvent> solveFriendDeleteEvent(FriendDeleteEvent event);
    Consumer<FriendInputStatusChangedEvent> solveFriendInputStatusChangedEvent(FriendInputStatusChangedEvent event);
    Consumer<FriendMessagePostSendEvent> solveFriendMessagePostSendEvent(FriendMessagePostSendEvent event);
    Consumer<FriendMessagePreSendEvent> solveFriendMessagePreSendEvent(FriendMessagePreSendEvent event);
    Consumer<FriendMessageSyncEvent> solveFriendMessageSyncEvent(FriendMessageSyncEvent event);
    Consumer<FriendNickChangedEvent> solveFriendNickChangedEvent(FriendNickChangedEvent event);
    Consumer<FriendRemarkChangeEvent> solveFriendRemarkChangeEvent(FriendRemarkChangeEvent event);
    Consumer<GroupAllowAnonymousChatEvent> solveGroupAllowAnonymousChatEvent(GroupAllowAnonymousChatEvent event);
    Consumer<GroupAllowConfessTalkEvent> solveGroupAllowConfessTalkEvent(GroupAllowConfessTalkEvent event);
    Consumer<GroupAllowMemberInviteEvent> solveGroupAllowMemberInviteEvent(GroupAllowMemberInviteEvent event);
    Consumer<GroupEntranceAnnouncementChangeEvent> solveGroupEntranceAnnouncementChangeEvent(GroupEntranceAnnouncementChangeEvent event);
    Consumer<GroupMessagePostSendEvent> solveGroupMessagePostSendEvent(GroupMessagePostSendEvent event);
    Consumer<GroupMessagePreSendEvent> solveGroupMessagePreSendEvent(GroupMessagePreSendEvent event);
    Consumer<GroupMessageSyncEvent> solveGroupMessageSyncEvent(GroupMessageSyncEvent event);
    Consumer<GroupMuteAllEvent> solveGroupMuteAllEvent(GroupMuteAllEvent event);
    Consumer<GroupNameChangeEvent> solveGroupNameChangeEvent(GroupNameChangeEvent event);
    Consumer<GroupTalkativeChangeEvent> solveGroupTalkativeChangeEvent(GroupTalkativeChangeEvent event);
    Consumer<GroupTempMessageEvent> solveGroupTempMessageEvent(GroupTempMessageEvent event);
    Consumer<GroupTempMessagePostSendEvent> solveGroupTempMessagePostSendEvent(GroupTempMessagePostSendEvent event);
    Consumer<GroupTempMessagePreSendEvent> solveGroupTempMessagePreSendEvent(GroupTempMessagePreSendEvent event);
    Consumer<GroupTempMessageSyncEvent> solveGroupTempMessageSyncEvent(GroupTempMessageSyncEvent event);
    Consumer<ImageUploadEvent> solveImageUploadEvent(ImageUploadEvent event);
    Consumer<MemberCardChangeEvent> solveMemberCardChangeEvent(MemberCardChangeEvent event);
    Consumer<MemberHonorChangeEvent> solveMemberHonorChangeEvent(MemberHonorChangeEvent event);
    Consumer<MemberJoinEvent> solveMemberJoinEvent(MemberJoinEvent event);
    Consumer<MemberJoinRequestEvent> solveMemberJoinRequestEvent(MemberJoinRequestEvent event);
    Consumer<MemberLeaveEvent> solveMemberLeaveEvent(MemberLeaveEvent event);
    Consumer<MemberMuteEvent> solveMemberMuteEvent(MemberMuteEvent event);
    Consumer<MemberPermissionChangeEvent> solveMemberPermissionChangeEvent(MemberPermissionChangeEvent event);
    Consumer<MemberSpecialTitleChangeEvent> solveMemberSpecialTitleChangeEvent(MemberSpecialTitleChangeEvent event);
    Consumer<MemberUnmuteEvent> solveMemberUnmuteEvent(MemberUnmuteEvent event);
    Consumer<NewFriendRequestEvent> solveNewFriendRequestEvent(NewFriendRequestEvent event);
    Consumer<NudgeEvent> solveNudgeEvent(NudgeEvent event);
    Consumer<OtherClientMessageEvent> solveOtherClientMessageEvent(OtherClientMessageEvent event);
    Consumer<OtherClientOfflineEvent> solveOtherClientOfflineEvent(OtherClientOfflineEvent event);
    Consumer<OtherClientOnlineEvent> solveOtherClientOnlineEvent(OtherClientOnlineEvent event);
    Consumer<StrangerAddEvent> solveStrangerAddEvent(StrangerAddEvent event);
    Consumer<StrangerMessageEvent> solveStrangerMessageEvent(StrangerMessageEvent event);
    Consumer<StrangerMessagePostSendEvent> solveStrangerMessagePostSendEvent(StrangerMessagePostSendEvent event);
    Consumer<StrangerMessagePreSendEvent> solveStrangerMessagePreSendEvent(StrangerMessagePreSendEvent event);
    Consumer<StrangerMessageSyncEvent> solveStrangerMessageSyncEvent(StrangerMessageSyncEvent event);
    Consumer<StrangerRelationChangeEvent> solveStrangerRelationChangeEvent(StrangerRelationChangeEvent event);

}