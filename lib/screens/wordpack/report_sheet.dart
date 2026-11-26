import 'package:flutter/material.dart';

import '../../data/models.dart';
import '../../utils/theme.dart';
import '../../widgets/buttons.dart';
import '../../widgets/inputs.dart';
import '../../widgets/sheets.dart';

/// 신고 사유 선택 시트. 단어팩·대결 상대 신고에 공용으로 쓴다. 신고하면 true.
Future<bool> showReportSheet(
  BuildContext context, {
  required String title,
  required String description,
  required List<String> reasons,
}) async {
  final result = await showAppSheet<bool>(context, (_) => _ReportForm(title, description, reasons));
  if (result == true && context.mounted) showToast(context, '신고가 접수되었어요');
  return result ?? false;
}

Future<void> showReportPackSheet(BuildContext context, WordPack pack) => showReportSheet(
      context,
      title: "'${pack.title}' 단어팩을 신고할까요?",
      description: '신고 내용은 검토 후 처리돼요.\n허위 신고 시 이용이 제한될 수 있어요.',
      reasons: const ['뜻이 틀렸거나 오타가 있어요', '부적절한 단어가 있어요', '광고·스팸이에요', '기타'],
    );

class _ReportForm extends StatefulWidget {
  const _ReportForm(this.title, this.description, this.reasons);

  final String title;
  final String description;
  final List<String> reasons;

  @override
  State<_ReportForm> createState() => _ReportFormState();
}

class _ReportFormState extends State<_ReportForm> {
  final _detail = TextEditingController();
  String? _selected;

  @override
  void dispose() {
    _detail.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(widget.title, style: AppText.title2),
        const SizedBox(height: 6),
        Text(widget.description, style: AppText.label2.copyWith(color: AppColors.textSecondary)),
        Gap.h16,
        for (final r in widget.reasons)
          InkWell(
            onTap: () => setState(() => _selected = r),
            borderRadius: AppRadius.sm,
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 10),
              child: Row(children: [
                CheckCircle(checked: _selected == r, size: 20),
                const SizedBox(width: 10),
                Text(r, style: AppText.label1),
              ]),
            ),
          ),
        Gap.h8,
        AppTextField(controller: _detail, hint: '자세한 내용 (선택)', maxLines: 4, minLines: 3),
        Gap.h20,
        AppButton('신고하기',
            kind: ButtonKind.danger, enabled: _selected != null, onPressed: () => Navigator.pop(context, true)),
        Gap.h8,
        AppButton('취소', kind: ButtonKind.secondary, onPressed: () => Navigator.pop(context, false)),
      ],
    );
  }
}
