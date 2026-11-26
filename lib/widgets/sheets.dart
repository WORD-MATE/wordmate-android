import 'package:flutter/material.dart';

import '../utils/theme.dart';
import 'buttons.dart';

/// 공통 바텀시트. [builder] 의 내용은 좌우 20 여백 안에 놓인다.
Future<T?> showAppSheet<T>(BuildContext context, WidgetBuilder builder) {
  return showModalBottomSheet<T>(
    context: context,
    isScrollControlled: true,
    useSafeArea: true,
    barrierColor: AppColors.scrim,
    builder: (ctx) => Padding(
      padding: EdgeInsets.only(bottom: MediaQuery.viewInsetsOf(ctx).bottom),
      child: SafeArea(
        top: false,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(Gap.screen, 24, Gap.screen, 12),
          child: builder(ctx),
        ),
      ),
    ),
  );
}

/// 제목/설명 + 확인/취소 시트. 확인이면 true.
Future<bool> showConfirmSheet(
  BuildContext context, {
  required String title,
  String? description,
  required String confirmText,
  String cancelText = '취소',
  ButtonKind confirmKind = ButtonKind.primary,
}) async {
  final result = await showAppSheet<bool>(
    context,
    (ctx) => Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(title, style: AppText.title2),
        if (description != null) ...[
          Gap.h8,
          Text(description, style: AppText.label1.copyWith(color: AppColors.textSecondary)),
        ],
        Gap.h24,
        AppButton(confirmText, kind: confirmKind, onPressed: () => Navigator.pop(ctx, true)),
        Gap.h8,
        AppButton(cancelText, kind: ButtonKind.secondary, onPressed: () => Navigator.pop(ctx, false)),
      ],
    ),
  );
  return result ?? false;
}

/// 가운데 다이얼로그. 확인이면 true.
Future<bool> showAppDialog(
  BuildContext context, {
  required String title,
  String? description,
  required String confirmText,
  String cancelText = '취소',
  ButtonKind confirmKind = ButtonKind.primary,
}) async {
  final result = await showDialog<bool>(
    context: context,
    barrierColor: AppColors.scrim,
    builder: (ctx) => Dialog(
      insetPadding: const EdgeInsets.symmetric(horizontal: 32),
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: AppText.title2),
            if (description != null) ...[
              Gap.h8,
              Text(description, style: AppText.label1.copyWith(color: AppColors.textSecondary)),
            ],
            Gap.h24,
            Row(children: [
              Expanded(
                child: AppButton(cancelText,
                    kind: ButtonKind.secondary, height: 48, onPressed: () => Navigator.pop(ctx, false)),
              ),
              Gap.w8,
              Expanded(
                child: AppButton(confirmText,
                    kind: confirmKind, height: 48, onPressed: () => Navigator.pop(ctx, true)),
              ),
            ]),
          ],
        ),
      ),
    ),
  );
  return result ?? false;
}

void showToast(BuildContext context, String message) {
  ScaffoldMessenger.of(context)
    ..hideCurrentSnackBar()
    ..showSnackBar(SnackBar(content: Text(message), duration: const Duration(seconds: 2)));
}
