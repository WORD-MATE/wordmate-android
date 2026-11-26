import 'package:flutter/material.dart';

void main() {
  runApp(const WordMateApp());
}

class WordMateApp extends StatelessWidget {
  const WordMateApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: 'WORD MATE',
      debugShowCheckedModeBanner: false,
      home: Scaffold(body: Center(child: Text('WORD MATE'))),
    );
  }
}
