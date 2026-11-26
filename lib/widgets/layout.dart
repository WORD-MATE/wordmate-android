import 'package:flutter/material.dart';

import '../utils/theme.dart';
import 'buttons.dart';

/// 뒤로가기/닫기 + 제목 + 우측 액션 상단바.
class AppTopBar extends StatelessWidget implements PreferredSizeWidget {
  const AppTopBar({super.key, this.title = '', this.onBack, this.onClose, this.actions = const []});

  final String title;
  final VoidCallback? onBack;
  final VoidCallback? onClose;
  final List<Widget> actions;

  @override
  Size get preferredSize => const Size.fromHeight(56);

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      bottom: false,
      child: SizedBox(
        height: 56,
        child: Row(
          children: [
            const SizedBox(width: 4),
            if (onBack != null)
              AppIconButton(Icons.arrow_back_ios_new_rounded, onPressed: onBack, tooltip: '뒤로', size: 22)
            else if (onClose != null)
              AppIconButton(Icons.close_rounded, onPressed: onClose, tooltip: '닫기')
            else
              const SizedBox(width: 12),
            Expanded(child: Text(title, style: AppText.title3, maxLines: 1, overflow: TextOverflow.ellipsis)),
            ...actions,
            const SizedBox(width: 4),
          ],
        ),
      ),
    );
  }
}

/// 하단 탭 루트 화면의 큰 제목 상단바.
class LargeTopBar extends StatelessWidget implements PreferredSizeWidget {
  const LargeTopBar({super.key, required this.title, this.actions = const [], this.titleStyle});

  final String title;
  final List<Widget> actions;
  final TextStyle? titleStyle;

  @override
  Size get preferredSize => const Size.fromHeight(56);

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      bottom: false,
      child: SizedBox(
        height: 56,
        child: Row(
          children: [
            const SizedBox(width: Gap.screen),
            Expanded(child: Text(title, style: titleStyle ?? AppText.title1)),
            ...actions,
            const SizedBox(width: 4),
          ],
        ),
      ),
    );
  }
}

/// 회색 배경 라운드 카드.
class AppCard extends StatelessWidget {
  const AppCard({
    super.key,
    required this.child,
    this.onTap,
    this.padding = const EdgeInsets.all(16),
    this.color = AppColors.surface,
    this.radius = AppRadius.lg,
    this.margin,
  });

  final Widget child;
  final VoidCallback? onTap;
  final EdgeInsetsGeometry padding;
  final Color color;
  final BorderRadius radius;
  final EdgeInsetsGeometry? margin;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: margin ?? EdgeInsets.zero,
      child: Material(
        color: color,
        borderRadius: radius,
        clipBehavior: Clip.antiAlias,
        child: InkWell(onTap: onTap, child: Padding(padding: padding, child: child)),
      ),
    );
  }
}

class SectionHeader extends StatelessWidget {
  const SectionHeader(this.title, {super.key, this.action, this.onAction, this.top = 12});

  final String title;
  final String? action;
  final VoidCallback? onAction;
  final double top;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.fromLTRB(Gap.screen, top + 12, Gap.screen - 4, 12),
      child: Row(
        children: [
          Expanded(child: Text(title, style: AppText.title2)),
          if (action != null)
            InkWell(
              onTap: onAction,
              borderRadius: AppRadius.sm,
              child: Padding(
                padding: const EdgeInsets.all(4),
                child: Row(children: [
                  Text(action!, style: AppText.label2.copyWith(color: AppColors.textSecondary)),
                  const Icon(Icons.chevron_right_rounded, size: 18, color: AppColors.textSecondary),
                ]),
              ),
            ),
        ],
      ),
    );
  }
}

/// 아이콘 + 제목 + 보조텍스트 + 화살표 메뉴 행.
class MenuRow extends StatelessWidget {
  const MenuRow(
    this.title, {
    super.key,
    this.icon,
    this.trailingText,
    this.titleColor = AppColors.textPrimary,
    this.chevron = true,
    this.trailing,
    this.onTap,
  });

  final String title;
  final IconData? icon;
  final String? trailingText;
  final Color titleColor;
  final bool chevron;
  final Widget? trailing;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen, vertical: 16),
        child: Row(
          children: [
            if (icon != null) ...[Icon(icon, size: 20, color: AppColors.textSecondary), Gap.w12],
            Expanded(child: Text(title, style: AppText.body2.copyWith(color: titleColor))),
            if (trailingText != null) ...[
              Text(trailingText!, style: AppText.label2.copyWith(color: AppColors.textTertiary)),
              Gap.w4,
            ],
            if (trailing != null)
              trailing!
            else if (chevron)
              const Icon(Icons.chevron_right_rounded, size: 22, color: AppColors.textTertiary),
          ],
        ),
      ),
    );
  }
}

class AppDivider extends StatelessWidget {
  const AppDivider({super.key, this.indent = 0});

  final double indent;

  @override
  Widget build(BuildContext context) =>
      Divider(height: 1, thickness: 1, color: AppColors.divider, indent: indent, endIndent: indent);
}

class ThickDivider extends StatelessWidget {
  const ThickDivider({super.key});

  @override
  Widget build(BuildContext context) => Container(height: 8, color: AppColors.surface);
}

/// 회색 사각 아바타.
class Avatar extends StatelessWidget {
  const Avatar({super.key, this.size = 40, this.background = Palette.gray06});

  final double size;
  final Color background;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(color: background, borderRadius: AppRadius.sm),
      child: Icon(Icons.person_rounded, size: size * 0.6, color: Palette.gray12),
    );
  }
}

/// 작은 텍스트 배지 ("24단어", "완료", "방장" 등).
class Tag extends StatelessWidget {
  const Tag(this.text, {super.key, this.background = AppColors.surfaceVariant, this.color = AppColors.textSecondary});

  final String text;
  final Color background;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
      decoration: BoxDecoration(color: background, borderRadius: AppRadius.xs),
      child: Text(text, style: AppText.caption1Bold.copyWith(color: color)),
    );
  }
}

class EmptyState extends StatelessWidget {
  const EmptyState({super.key, required this.icon, required this.message});

  final IconData icon;
  final String message;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 64),
      child: Column(
        children: [
          Container(
            width: 56,
            height: 56,
            decoration: const BoxDecoration(color: AppColors.surfaceVariant, borderRadius: AppRadius.md),
            child: Icon(icon, color: AppColors.info, size: 28),
          ),
          Gap.h12,
          Text(message, style: AppText.label1.copyWith(color: AppColors.textTertiary)),
        ],
      ),
    );
  }
}

/// 라운드 진행 바.
class ProgressBar extends StatelessWidget {
  const ProgressBar({
    super.key,
    required this.value,
    this.height = 6,
    this.color = AppColors.primary,
    this.track = Palette.gray06,
  });

  final double value;
  final double height;
  final Color color;
  final Color track;

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(height),
      child: SizedBox(
        height: height,
        child: TweenAnimationBuilder<double>(
          tween: Tween(end: value.clamp(0.0, 1.0)),
          duration: const Duration(milliseconds: 250),
          builder: (_, v, _) => LinearProgressIndicator(
            value: v,
            minHeight: height,
            color: color,
            backgroundColor: track,
          ),
        ),
      ),
    );
  }
}

/// 숫자 + 작은 단위 ("7 일", "86 /500").
class ValueUnit extends StatelessWidget {
  const ValueUnit(this.value, this.unit, {super.key, this.style, this.color = AppColors.textPrimary});

  final String value;
  final String unit;
  final TextStyle? style;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Text.rich(TextSpan(children: [
      TextSpan(text: value, style: (style ?? AppText.title2).copyWith(color: color)),
      TextSpan(text: ' $unit', style: AppText.caption1.copyWith(color: AppColors.textTertiary)),
    ]));
  }
}
