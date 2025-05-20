export default {
  namespaced: true,
  state: () => ({
    wordList: [
      {
        romaji: "oboe",
        kana: "おぼえ",
        kanji: "覚え",
        meaning: "ghi nhớ; nhớ; kinh nghiệm; tự tin",
      },
      {
        romaji: "obon",
        kana: "おぼん",
        kanji: "お盆",
        meaning: "lễ Obon; mâm; khay",
      },
      {
        romaji: "oboru",
        kana: "おぼろ",
        kanji: "朧う",
        meaning: "hazy, dim, faint; minced meat or fish",
      },
      {
        romaji: "oboko",
        kana: "おぼこ",
        kanji: "童女",
        meaning: "gái trinh, gái đồng trinh",
      },
      {
        romaji: "oboko",
        kana: "おぼこ",
        kanji: "未通女",
        meaning: "gái trinh",
      },
      {
        romaji: "oborozuki",
        kana: "おぼろづき",
        kanji: "朧夜",
        meaning: "misty, đêm sáng trăng",
      },
      {
        romaji: "oboroge",
        kana: "おぼろげ",
        kanji: "朧気",
        meaning: "tính chất mờ mờ; trạng thái mơ hồ",
      },
      {
        romaji: "oboeru",
        kana: "おぼえず",
        kanji: "覚えず",
        meaning: "unconsciously, unknowingly",
      },
      {
        romaji: "oborezu",
        kana: "おぼれず",
        kanji: "溺らず",
        meaning: "(1) dễ chết đuối; (2) tội nguyên nhân sẽ gây hại",
      },
      {
        romaji: "oboeru",
        kana: "おぼえる",
        kanji: "憶える",
        meaning: "nhớ; thu thập lại",
      },
    ],
    KanjiList: [
      {
        kanjiname: "Giao",
        kanji: "交",
        reading: "コウ",
        kunyomi: "まじる/まじえる/ま.ぜる/ま.じる",
      },
      {
        kanjiname: "Giao",
        kanji: "郊",
        reading: "コウ",
        kunyomi: "",
      },
      {
        kanjiname: "Giao",
        kanji: "蛍",
        reading: "コウ, キョウ",
        kunyomi: "みずち",
      },
      {
        kanjiname: "Giao",
        kanji: "鮫",
        reading: "コウ",
        kunyomi: "さめ, みずち",
      },
      {
        kanjiname: "Giao",
        kanji: "攪",
        reading: "カク, コウ",
        kunyomi: "みだす",
      },
    ],
    grammar: [
      {
        romaji: "tame ni",
        kana: "〜ために",
        meaning: "Vì",
      },
      {
        romaji: "aratameru",
        kana: "あらためる",
        meaning: "Lại",
      },
      {
        romaji: "tame",
        kana: "... がため",
        meaning: "Để ...",
      },
      {
        romaji: "tame",
        kana: "... ため",
        meaning: "Bởi, vì",
      },
      {
        romaji: "tameshiganai",
        kana: "ためしがない",
        meaning: "Chưa hề ...",
      },
      {
        romaji: "tameni",
        kana: "〜ために",
        meaning: "Để~, cho~, vì~",
      },
      {
        romaji: "tameshitemiru",
        kana: "ためしに…てみる",
        meaning: "Thử ... xem sao",
      },
      {
        romaji: "tameno",
        kana: "〜んがため（に）/〜んがための",
        meaning: "Với mục đích để~",
      },
      {
        romaji: "hitotsu ni wa tame de aru",
        kana: "ひとつには…ためである",
        meaning: "Một phần là do ...",
      },
      {
        romaji: "nowatameda",
        kana: "のは…ためだ",
        meaning: "Là vì, là để ...",
      },
    ],
    sentenceList: [
      {
        sentence:
          "あなたの口座がある銀行が所有しないATM現金自動預け入れ支払機でキャッシュ・カードを使えば、使用者手数料を払わなければ...",
        translation:
          "Nếu bạn sử dụng thẻ tại ATM không thuộc ngân hàng của mình, bạn sẽ phải trả phí giao dịch...",
      },
      {
        sentence:
          "サム、あなたがいなくて毎日寂しい思いをしています。でも、私たちの置かれている状況を整理して、二人が一緒にいて両方が本当...",
        translation:
          "Sam à, mỗi ngày anh không có ở đây, em đều cảm thấy rất cô đơn. Nhưng chúng ta cần xem xét lại tình huống hiện tại để cả hai có thể thật sự bên nhau...",
      },
      {
        sentence:
          "あなたの次の職場は人手不足なので、最初から全力で仕事しなければならないだろう",
        translation:
          "Nơi làm việc tiếp theo của bạn đang thiếu nhân lực, nên bạn có lẽ sẽ phải làm hết sức mình ngay từ đầu.",
      },
      {
        sentence: "ルーシーの代わりに、ジェーンが今夜あなたのお世話をします",
        translation: "Tối nay Jane sẽ chăm sóc bạn thay cho Lucy.",
      },
      {
        sentence: "仮に私があなたの立場なら",
        translation: "Giả sử tôi ở trong vị trí của bạn...",
      },
      {
        sentence:
          "「タイの人々は地味な服装をしていますね」「あなたももしお寺巡りをしたいなら派手な服装は避けた方がいいですよ」",
        translation:
          "“Người Thái ăn mặc khá giản dị nhỉ.” “Nếu bạn cũng muốn tham quan chùa thì nên tránh mặc đồ sặc sỡ nhé.”",
      },
      {
        sentence: "あなたが思い切ってあの状況から脱してくれてうれしい。",
        translation:
          "Tôi thật sự mừng vì bạn đã dũng cảm thoát ra khỏi tình huống đó.",
      },
      {
        sentence: "あなたみたいにきれいな人に会ったのは初めてです",
        translation: "Đây là lần đầu tiên tôi gặp một người xinh đẹp như bạn.",
      },
      {
        sentence:
          "会社であなたに会えなくなるのはとても寂しいけど、一年間海外で働くというあなたの夢が実現したこと là素晴らしいと思うわ。",
        translation:
          "Không gặp bạn ở công ty nữa thật sự khiến tôi rất buồn, nhưng tôi nghĩ thật tuyệt khi bạn đã thực hiện được ước mơ làm việc ở nước ngoài trong một năm.",
      },
      {
        sentence:
          "あなたの家にいると、まるでふるさとに戻ったようにゆったりとくつろぐ",
        translation:
          "Khi ở nhà bạn, tôi có cảm giác thư thái như thể mình đã trở về quê hương.",
      },
    ],
  }),
  getters: {
    wordList: (state) => state.wordList,
  },
};
