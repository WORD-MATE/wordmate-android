import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../data/store.dart';
import '../../utils/theme.dart';
import '../../widgets/layout.dart';

/// 홈 우측 상단 종을 누르면 나오는 알림 팝오버 (Figma "알림").
Future<void> showNotificationPopup(BuildContext context, {required VoidCallback onBattleInvite}) {
  return showGeneralDialog(
    context: context,
    barrierDismissible: true,
    barrierLabel: '알림 닫기',
    barrierColor: Colors.transparent,
    transitionDuration: const Duration(milliseconds: 150),
    pageBuilder: (ctx, _, _) => SafeArea(
      child: Align(
        alignment: Alignment.topCenter,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 52, 16, 0),
          child: Material(
            color: AppColors.background,
            elevation: 12,
            shadowColor: Colors.black26,
            borderRadius: AppRadius.lg,
            clipBehavior: Clip.antiAlias,
            child: ListenableBuilder(
              listenable: store,
              builder: (ctx, _) => Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(16, 12, 8, 12),
                    child: Row(children: [
                      Text('알림', style: AppText.body2Bold),
                      const SizedBox(width: 6),
                      if (store.unreadCount > 0)
                        Tag('${store.unreadCount}', background: AppColors.primary, color: AppColors.onPrimary),
                      const Spacer(),
                      TextButton(
                        onPressed: store.markAllRead,
                        child: Text('모두 읽음', style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
                      ),
                      const Icon(Icons.settings_rounded, size: 20, color: AppColors.textPrimary),
                      const SizedBox(width: 8),
                    ]),
                  ),
                  const AppDivider(),
                  for (final n in store.notifications)
                    _NotificationRow(
                      item: n,
                      onTap: () {
                        store.markRead(n.id);
                        Navigator.pop(ctx);
                        if (n.type == NotificationType.battleInvite) onBattleInvite();
                      },
                    ),
                  Gap.h8,
                ],
              ),
            ),
          ),
        ),
      ),
    ),
  );
}

class _NotificationRow extends StatelessWidget {
  const _NotificationRow({required this.item, required this.onTap});

  final AppNotification item;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final (icon, color) = switch (item.type) {
      NotificationType.battleInvite => (Icons.bolt_rounded, Palette.yellow02),
      NotificationType.dailySentence => (Icons.wb_sunny_rounded, Palette.orange06),
      NotificationType.friendRequest => (Icons.person_add_rounded, AppColors.info),
    };
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Container(
            width: 36,
            height: 36,
            decoration: const BoxDecoration(color: AppColors.surfaceVariant, borderRadius: AppRadius.sm),
            child: Icon(icon, color: color, size: 20),
          ),
          Gap.w12,
          Expanded(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Row(children: [
                Text(item.title, style: AppText.label1Bold),
                if (!item.read) ...[
                  Gap.w4,
                  Container(
                    width: 5,
                    height: 5,
                    decoration: const BoxDecoration(color: AppColors.notification, shape: BoxShape.circle),
                  ),
                ],
              ]),
              const SizedBox(height: 2),
              Text(item.body,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: AppText.caption1.copyWith(color: AppColors.textSecondary)),
              const SizedBox(height: 2),
              Text(item.timeAgo, style: AppText.caption2.copyWith(color: AppColors.textTertiary)),
            ]),
          ),
          if (item.read) Text('읽음', style: AppText.caption2.copyWith(color: AppColors.textTertiary)),
        ]),
      ),
    );
  }
}
