import 'package:flutter/material.dart';

import '../utils/theme.dart';

enum ButtonKind { primary, dark, secondary, outline, danger, mint }

/// Figma "05 · Actions & Inputs" 기본 버튼.
class AppButton extends StatelessWidget {
  const AppButton(
    this.text, {
    super.key,
    required this.onPressed,
    this.kind = ButtonKind.primary,
    this.enabled = true,
    this.height = 52,
    this.leading,
    this.trailing,
    this.expand = true,
  });

  final String text;
  final VoidCallback? onPressed;
  final ButtonKind kind;
  final bool enabled;
  final double height;
  final IconData? leading;
  final IconData? trailing;
  final bool expand;

  (Color, Color, Color?) get _colors {
    if (!enabled || onPressed == null) return (AppColors.surfaceVariant, AppColors.textDisabled, null);
    return switch (kind) {
      ButtonKind.primary => (AppColors.primary, AppColors.onPrimary, null),
      ButtonKind.dark => (AppColors.black, Palette.gray01, null),
      ButtonKind.secondary => (AppColors.surfaceVariant, AppColors.textPrimary, null),
      ButtonKind.outline => (AppColors.background, AppColors.textPrimary, AppColors.border),
      ButtonKind.danger => (AppColors.errorStrong, Palette.gray01, null),
      ButtonKind.mint => (const Color(0xFFD9F7E9), AppColors.textPrimary, null),
    };
  }

  @override
  Widget build(BuildContext context) {
    final (bg, fg, border) = _colors;
    final active = enabled && onPressed != null;
    return SizedBox(
      height: height,
      width: expand ? double.infinity : null,
      child: Material(
        color: bg,
        shape: RoundedRectangleBorder(
          borderRadius: AppRadius.md,
          side: border == null ? BorderSide.none : BorderSide(color: border),
        ),
        clipBehavior: Clip.antiAlias,
        child: InkWell(
          onTap: active ? onPressed : null,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              mainAxisSize: expand ? MainAxisSize.max : MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                if (leading != null) ...[Icon(leading, size: 18, color: fg), const SizedBox(width: 6)],
                Flexible(
                  child: Text(text, style: AppText.body2Bold.copyWith(color: fg), overflow: TextOverflow.ellipsis),
                ),
                if (trailing != null) ...[const SizedBox(width: 6), Icon(trailing, size: 18, color: fg)],
              ],
            ),
          ),
        ),
      ),
    );
  }
}

/// 작은 버튼 (XP 받기, 퀴즈 풀기 등).
class SmallButton extends StatelessWidget {
  const SmallButton(this.text, {super.key, required this.onPressed, this.kind = ButtonKind.primary, this.trailing});

  final String text;
  final VoidCallback onPressed;
  final ButtonKind kind;
  final IconData? trailing;

  @override
  Widget build(BuildContext context) =>
      AppButton(text, onPressed: onPressed, kind: kind, height: 36, trailing: trailing, expand: false);
}

/// 화면 하단 고정 CTA.
class BottomCta extends StatelessWidget {
  const BottomCta(this.text, {super.key, required this.onPressed, this.enabled = true, this.kind = ButtonKind.primary});

  final String text;
  final VoidCallback? onPressed;
  final bool enabled;
  final ButtonKind kind;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(Gap.screen, 12, Gap.screen, 12),
        child: AppButton(text, onPressed: onPressed, enabled: enabled, kind: kind),
      ),
    );
  }
}

/// 원형 리플 아이콘 버튼. [badge] 가 true 면 알림 점을 표시한다.
class AppIconButton extends StatelessWidget {
  const AppIconButton(
    this.icon, {
    super.key,
    required this.onPressed,
    this.tooltip,
    this.color = AppColors.textPrimary,
    this.badge = false,
    this.size = 24,
  });

  final IconData icon;
  final VoidCallback? onPressed;
  final String? tooltip;
  final Color color;
  final bool badge;
  final double size;

  @override
  Widget build(BuildContext context) {
    return IconButton(
      onPressed: onPressed,
      tooltip: tooltip,
      icon: Stack(
        clipBehavior: Clip.none,
        children: [
          Icon(icon, color: color, size: size),
          if (badge)
            Positioned(
              right: -1,
              top: -1,
              child: Container(
                width: 7,
                height: 7,
                decoration: const BoxDecoration(color: AppColors.notification, shape: BoxShape.circle),
              ),
            ),
        ],
      ),
    );
  }
}
