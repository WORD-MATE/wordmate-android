import 'package:flutter/material.dart';

import '../utils/theme.dart';

/// 회색 배경의 기본 입력창.
class AppTextField extends StatelessWidget {
  const AppTextField({
    super.key,
    required this.controller,
    required this.hint,
    this.onChanged,
    this.onSubmitted,
    this.error = false,
    this.maxLines = 1,
    this.minLines,
    this.prefixIcon,
    this.autofocus = false,
    this.enabled = true,
    this.textInputAction,
    this.maxLength,
  });

  final TextEditingController controller;
  final String hint;
  final ValueChanged<String>? onChanged;
  final ValueChanged<String>? onSubmitted;
  final bool error;
  final int? maxLines;
  final int? minLines;
  final IconData? prefixIcon;
  final bool autofocus;
  final bool enabled;
  final TextInputAction? textInputAction;
  final int? maxLength;

  @override
  Widget build(BuildContext context) {
    OutlineInputBorder border(Color c) =>
        OutlineInputBorder(borderRadius: AppRadius.md, borderSide: BorderSide(color: c));
    return TextField(
      controller: controller,
      onChanged: onChanged,
      onSubmitted: onSubmitted,
      autofocus: autofocus,
      enabled: enabled,
      maxLines: maxLines,
      minLines: minLines,
      maxLength: maxLength,
      textInputAction: textInputAction,
      style: AppText.body2,
      cursorColor: AppColors.textPrimary,
      decoration: InputDecoration(
        hintText: hint,
        hintStyle: AppText.body2.copyWith(color: AppColors.textTertiary),
        filled: true,
        fillColor: error ? const Color(0xFFFFF4F4) : AppColors.surfaceVariant,
        counterText: '',
        isDense: true,
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 15),
        prefixIcon: prefixIcon == null ? null : Icon(prefixIcon, color: AppColors.textTertiary, size: 20),
        prefixIconConstraints: const BoxConstraints(minWidth: 44),
        enabledBorder: border(error ? AppColors.error : Colors.transparent),
        disabledBorder: border(error ? AppColors.error : Colors.transparent),
        focusedBorder: border(error ? AppColors.error : AppColors.border),
      ),
    );
  }
}

class SearchField extends StatelessWidget {
  const SearchField({super.key, required this.controller, required this.hint, required this.onChanged});

  final TextEditingController controller;
  final String hint;
  final ValueChanged<String> onChanged;

  @override
  Widget build(BuildContext context) => AppTextField(
        controller: controller,
        hint: hint,
        onChanged: onChanged,
        prefixIcon: Icons.search_rounded,
        textInputAction: TextInputAction.search,
      );
}

/// 검정 채움(선택) / 테두리(기본) 필터 칩.
class AppChip extends StatelessWidget {
  const AppChip(this.text, {super.key, required this.selected, required this.onTap, this.count, this.dot});

  final String text;
  final bool selected;
  final VoidCallback onTap;
  final int? count;
  final Color? dot;

  @override
  Widget build(BuildContext context) {
    final fg = selected ? Palette.gray01 : AppColors.textSecondary;
    return Material(
      color: selected ? AppColors.black : AppColors.background,
      shape: RoundedRectangleBorder(
        borderRadius: AppRadius.sm,
        side: BorderSide(color: selected ? AppColors.black : AppColors.border),
      ),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Container(
          height: 34,
          padding: const EdgeInsets.symmetric(horizontal: 12),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (dot != null) ...[
                Container(width: 6, height: 6, decoration: BoxDecoration(color: dot, shape: BoxShape.circle)),
                const SizedBox(width: 6),
              ],
              Text(text, style: AppText.label2Bold.copyWith(color: fg)),
              if (count != null) ...[
                const SizedBox(width: 4),
                Text('$count',
                    style: AppText.label2.copyWith(color: selected ? Palette.gray11 : AppColors.textTertiary)),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

/// 가로 스크롤 칩 목록.
class ChipRow<T> extends StatelessWidget {
  const ChipRow({
    super.key,
    required this.items,
    required this.selected,
    required this.label,
    required this.onSelect,
    this.count,
    this.dot,
  });

  final List<T> items;
  final T selected;
  final String Function(T) label;
  final ValueChanged<T> onSelect;
  final int? Function(T)? count;
  final Color? Function(T)? dot;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 34,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: Gap.screen),
        itemCount: items.length,
        separatorBuilder: (_, _) => const SizedBox(width: 6),
        itemBuilder: (_, i) {
          final item = items[i];
          return AppChip(
            label(item),
            selected: item == selected,
            onTap: () => onSelect(item),
            count: count?.call(item),
            dot: dot?.call(item),
          );
        },
      ),
    );
  }
}

/// 회색 트랙 위 흰색 선택 탭 (기본 단어팩 / 내 단어팩 등).
class SegmentedTabs extends StatelessWidget {
  const SegmentedTabs({super.key, required this.tabs, required this.index, required this.onSelect});

  final List<String> tabs;
  final int index;
  final ValueChanged<int> onSelect;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: const BoxDecoration(color: AppColors.surfaceVariant, borderRadius: AppRadius.md),
      child: Row(
        children: [
          for (var i = 0; i < tabs.length; i++)
            Expanded(
              child: GestureDetector(
                behavior: HitTestBehavior.opaque,
                onTap: () => onSelect(i),
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 150),
                  height: 36,
                  alignment: Alignment.center,
                  decoration: BoxDecoration(
                    color: i == index ? AppColors.background : Colors.transparent,
                    borderRadius: AppRadius.sm,
                  ),
                  child: Text(
                    tabs[i],
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: (i == index ? AppText.label1Bold : AppText.label1)
                        .copyWith(color: i == index ? AppColors.textPrimary : AppColors.textTertiary),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}

/// 원형 체크 (약관, 라디오 선택).
class CheckCircle extends StatelessWidget {
  const CheckCircle({super.key, required this.checked, this.size = 22});

  final bool checked;
  final double size;

  @override
  Widget build(BuildContext context) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 150),
      width: size,
      height: size,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: checked ? AppColors.primary : AppColors.background,
        border: Border.all(color: checked ? AppColors.primary : AppColors.border, width: 1.5),
      ),
      child: checked ? Icon(Icons.check_rounded, size: size * 0.7, color: AppColors.onPrimary) : null,
    );
  }
}

class AppSwitch extends StatelessWidget {
  const AppSwitch({super.key, required this.value, required this.onChanged});

  final bool value;
  final ValueChanged<bool> onChanged;

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: value,
      onChanged: onChanged,
      thumbColor: const WidgetStatePropertyAll(Palette.gray01),
      trackColor: WidgetStateProperty.resolveWith(
        (s) => s.contains(WidgetState.selected) ? AppColors.primary : Palette.gray10,
      ),
      trackOutlineColor: const WidgetStatePropertyAll(Colors.transparent),
      materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
    );
  }
}
