export default {
  namespaced: true,
  state: () => ({
    wordList: [
      {
        id: 1,
        romaji: "oboe",
        kana: "おぼえ",
        kanji: "覚え",
        meaning: "ghi nhớ; nhớ; kinh nghiệm; tự tin",
        comments: [
          {
            id: 1,
            content: "Từ này hay dùng trong ngữ cảnh học tập, ví dụ: 漢字を覚える (học thuộc kanji)",
            userId: "user1",
            username: "Minh Anh",
            userAvatar: "https://i.pravatar.cc/150?img=1",
            timestamp: "2024-03-15T08:30:00.000Z"
          },
          {
            id: 2,
            content: "Mình thường nhớ từ này qua câu 日本語を覚えましょう (Hãy học tiếng Nhật)",
            userId: "user2",
            username: "Thanh Hà",
            userAvatar: "https://i.pravatar.cc/150?img=2",
            timestamp: "2024-03-15T09:15:00.000Z"
          },
          {
            id: 3,
            content: "Có thể dùng từ này khi muốn nói về việc ghi nhớ một kỹ năng mới",
            userId: "user3",
            username: "Hoàng Nam",
            userAvatar: "https://i.pravatar.cc/150?img=3",
            timestamp: "2024-03-15T10:00:00.000Z"
          },
          {
            id: 4,
            content: "覚える là động từ nhóm 2, biến thể quá khứ là 覚えた",
            userId: "user4",
            username: "Mai Linh",
            userAvatar: "https://i.pravatar.cc/150?img=4",
            timestamp: "2024-03-15T10:45:00.000Z"
          }
        ]
      },
      {
        id: 2,
        romaji: "obon",
        kana: "おぼん",
        kanji: "お盆",
        meaning: "lễ Obon; mâm; khay",
        comments: [
          {
            id: 1,
            content: "Lễ Obon là một ngày được tôn vinh cho các ông bố và ông nội",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user6",
            username: "Thu Trang",
            userAvatar: "https://i.pravatar.cc/150?img=6",
            timestamp: "2024-03-15T12:15:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user7",
            username: "Quang Minh",
            userAvatar: "https://i.pravatar.cc/150?img=7",
            timestamp: "2024-03-15T13:00:00.000Z"
          },
          
        ]
      },
      {
        id: 3,
        romaji: "oboru",
        kana: "おぼろ",
        kanji: "朧う",
        meaning: "hazy, dim, faint; minced meat or fish",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user8",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=8",
            timestamp: "2024-03-15T13:45:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user9",
            username: "Tuấn Kiệt",
            userAvatar: "https://i.pravatar.cc/150?img=9",
            timestamp: "2024-03-15T14:30:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 4,
        romaji: "oboko",
        kana: "おぼこ",
        kanji: "童女",
        meaning: "gái trinh, gái đồng trinh",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user11",
            username: "Văn Hùng",
            userAvatar: "https://i.pravatar.cc/150?img=11",
            timestamp: "2024-03-15T16:00:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user12",
            username: "Lan Anh",
            userAvatar: "https://i.pravatar.cc/150?img=12",
            timestamp: "2024-03-15T16:45:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user13",
            username: "Thành Đạt",
            userAvatar: "https://i.pravatar.cc/150?img=13",
            timestamp: "2024-03-15T17:30:00.000Z"
          }
        ]
      },
      {
        id: 5,
        romaji: "oboko",
        kana: "おぼこ",
        kanji: "未通女",
        meaning: "gái trinh",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user14",
            username: "Minh Tuấn",
            userAvatar: "https://i.pravatar.cc/150?img=14",
            timestamp: "2024-03-15T18:15:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user15",
            username: "Thùy Linh",
            userAvatar: "https://i.pravatar.cc/150?img=15",
            timestamp: "2024-03-15T19:00:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user16",
            username: "Đức Thắng",
            userAvatar: "https://i.pravatar.cc/150?img=16",
            timestamp: "2024-03-15T19:45:00.000Z"
          }
        ]
      },
      {
        id: 6,
        romaji: "oborozuki",
        kana: "おぼろづき",
        kanji: "朧夜",
        meaning: "misty, đêm sáng trăng",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user17",
            username: "Hương Giang",
            userAvatar: "https://i.pravatar.cc/150?img=17",
            timestamp: "2024-03-15T20:30:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội", 
            userId: "user18",
            username: "Quốc Bảo",
            userAvatar: "https://i.pravatar.cc/150?img=18",
            timestamp: "2024-03-15T21:15:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user19",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=19",
            timestamp: "2024-03-15T22:00:00.000Z"
          }
        ]
      },
      {
        id: 7,
        romaji: "oboroge",
        kana: "おぼろげ",
        kanji: "朧気",
        meaning: "tính chất mờ mờ; trạng thái mơ hồ",
        comments: [
          {
            id: 1,
            content: "Từ này thường được dùng để miêu tả ký ức mơ hồ",
            userId: "user20",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=20",
            timestamp: "2024-03-15T22:45:00.000Z"
          }
        ]
      },
      {
        id: 8,
        romaji: "oboeru",
        kana: "おぼえる",
        kanji: "覚える",
        meaning: "học; ghi nhớ; nhớ",
        comments: [
          {
            id: 1,
            content: "Đây là động từ nhóm 2, thường dùng trong ngữ cảnh học tập",
            userId: "user21",
            username: "Quốc Bảo",
            userAvatar: "https://i.pravatar.cc/150?img=21",
            timestamp: "2024-03-15T23:30:00.000Z"
          }
        ]
      },
      {
        id: 9,
        romaji: "oboreru",
        kana: "おぼれる",
        kanji: "溺れる",
        meaning: "chìm; đắm; say mê",
        comments: [
          {
            id: 1,
            content: "Có thể dùng theo nghĩa đen (chìm trong nước) hoặc nghĩa bóng (say mê)",
            userId: "user22",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=22",
            timestamp: "2024-03-15T24:15:00.000Z"
          }
        ]
      },
      {
        id: 10,
        romaji: "oboro",
        kana: "おぼろ",
        kanji: "朧",
        meaning: "mờ ảo; không rõ ràng",
        comments: [
          {
            id: 1,
            content: "Thường được dùng để miêu tả ánh trăng mờ ảo",
            userId: "user23",
            username: "Minh Tuấn",
            userAvatar: "https://i.pravatar.cc/150?img=23",
            timestamp: "2024-03-16T01:00:00.000Z"
          }
        ]
      },
      {
        id: 11,
        romaji: "oboe",
        kana: "おぼえ",
        kanji: "覚え",
        meaning: "ghi nhớ; nhớ; kinh nghiệm; tự tin",
        comments: [
          {
            id: 1,
            content: "Từ này hay dùng trong ngữ cảnh học tập, ví dụ: 漢字を覚える (học thuộc kanji)",
            userId: "user1",
            username: "Minh Anh",
            userAvatar: "https://i.pravatar.cc/150?img=1",
            timestamp: "2024-03-15T08:30:00.000Z"
          },
          {
            id: 2,
            content: "Mình thường nhớ từ này qua câu 日本語を覚えましょう (Hãy học tiếng Nhật)",
            userId: "user2",
            username: "Thanh Hà",
            userAvatar: "https://i.pravatar.cc/150?img=2",
            timestamp: "2024-03-15T09:15:00.000Z"
          },
          {
            id: 3,
            content: "Có thể dùng từ này khi muốn nói về việc ghi nhớ một kỹ năng mới",
            userId: "user3",
            username: "Hoàng Nam",
            userAvatar: "https://i.pravatar.cc/150?img=3",
            timestamp: "2024-03-15T10:00:00.000Z"
          },
          {
            id: 4,
            content: "覚える là động từ nhóm 2, biến thể quá khứ là 覚えた",
            userId: "user4",
            username: "Mai Linh",
            userAvatar: "https://i.pravatar.cc/150?img=4",
            timestamp: "2024-03-15T10:45:00.000Z"
          }
        ]
      },
      {
        id: 12,
        romaji: "obon",
        kana: "おぼん",
        kanji: "お盆",
        meaning: "lễ Obon; mâm; khay",
        comments: [
          {
            id: 1,
            content: "Lễ Obon là một ngày được tôn vinh cho các ông bố và ông nội",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user6",
            username: "Thu Trang",
            userAvatar: "https://i.pravatar.cc/150?img=6",
            timestamp: "2024-03-15T12:15:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user7",
            username: "Quang Minh",
            userAvatar: "https://i.pravatar.cc/150?img=7",
            timestamp: "2024-03-15T13:00:00.000Z"
          },
          
        ]
      },
      {
        id: 13,
        romaji: "oboru",
        kana: "おぼろ",
        kanji: "朧う",
        meaning: "hazy, dim, faint; minced meat or fish",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user8",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=8",
            timestamp: "2024-03-15T13:45:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user9",
            username: "Tuấn Kiệt",
            userAvatar: "https://i.pravatar.cc/150?img=9",
            timestamp: "2024-03-15T14:30:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 14,
        romaji: "oboko",
        kana: "おぼこ",
        kanji: "童女",
        meaning: "gái trinh, gái đồng trinh",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user11",
            username: "Văn Hùng",
            userAvatar: "https://i.pravatar.cc/150?img=11",
            timestamp: "2024-03-15T16:00:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user12",
            username: "Lan Anh",
            userAvatar: "https://i.pravatar.cc/150?img=12",
            timestamp: "2024-03-15T16:45:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user13",
            username: "Thành Đạt",
            userAvatar: "https://i.pravatar.cc/150?img=13",
            timestamp: "2024-03-15T17:30:00.000Z"
          }
        ]
      },
      {
        id: 15,
        romaji: "oboko",
        kana: "おぼこ",
        kanji: "未通女",
        meaning: "gái trinh",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user14",
            username: "Minh Tuấn",
            userAvatar: "https://i.pravatar.cc/150?img=14",
            timestamp: "2024-03-15T18:15:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user15",
            username: "Thùy Linh",
            userAvatar: "https://i.pravatar.cc/150?img=15",
            timestamp: "2024-03-15T19:00:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user16",
            username: "Đức Thắng",
            userAvatar: "https://i.pravatar.cc/150?img=16",
            timestamp: "2024-03-15T19:45:00.000Z"
          }
        ]
      },
      {
        id: 16,
        romaji: "oborozuki",
        kana: "おぼろづき",
        kanji: "朧夜",
        meaning: "misty, đêm sáng trăng",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user17",
            username: "Hương Giang",
            userAvatar: "https://i.pravatar.cc/150?img=17",
            timestamp: "2024-03-15T20:30:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội", 
            userId: "user18",
            username: "Quốc Bảo",
            userAvatar: "https://i.pravatar.cc/150?img=18",
            timestamp: "2024-03-15T21:15:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user19",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=19",
            timestamp: "2024-03-15T22:00:00.000Z"
          }
        ]
      },
      {
        id: 17,
        romaji: "oboroge",
        kana: "おぼろげ",
        kanji: "朧気",
        meaning: "tính chất mờ mờ; trạng thái mơ hồ",
        comments: [
          {
            id: 1,
            content: "Từ này thường được dùng để miêu tả ký ức mơ hồ",
            userId: "user20",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=20",
            timestamp: "2024-03-15T22:45:00.000Z"
          }
        ]
      },
      {
        id: 18,
        romaji: "oboeru",
        kana: "おぼえる",
        kanji: "覚える",
        meaning: "học; ghi nhớ; nhớ",
        comments: [
          {
            id: 1,
            content: "Đây là động từ nhóm 2, thường dùng trong ngữ cảnh học tập",
            userId: "user21",
            username: "Quốc Bảo",
            userAvatar: "https://i.pravatar.cc/150?img=21",
            timestamp: "2024-03-15T23:30:00.000Z"
          }
        ]
      },
      {
        id: 19,
        romaji: "oboreru",
        kana: "おぼれる",
        kanji: "溺れる",
        meaning: "chìm; đắm; say mê",
        comments: [
          {
            id: 1,
            content: "Có thể dùng theo nghĩa đen (chìm trong nước) hoặc nghĩa bóng (say mê)",
            userId: "user22",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=22",
            timestamp: "2024-03-15T24:15:00.000Z"
          }
        ]
      },
      {
        id: 20,
        romaji: "oboro",
        kana: "おぼろ",
        kanji: "朧",
        meaning: "mờ ảo; không rõ ràng",
        comments: [
          {
            id: 1,
            content: "Thường được dùng để miêu tả ánh trăng mờ ảo",
            userId: "user23",
            username: "Minh Tuấn",
            userAvatar: "https://i.pravatar.cc/150?img=23",
            timestamp: "2024-03-16T01:00:00.000Z"
          }
        ]
      },
    ],
    KanjiList: [
      {
        id: 1,
        kanjiname: "Giao",
        kanji: "交",
        reading: "コウ",
        kunyomi: "まじる/まじえる/ま.ぜる/ま.じる",
        comments: [
          {
            id: 1,
            content: "Kanji này thường xuất hiện trong từ 交通 (giao thông)",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          },
          {
            id: 2,
            content: "Bộ thủ của chữ này là 亠, có nghĩa là mái nhà",
            userId: "user6",
            username: "Thu Trang",
            userAvatar: "https://i.pravatar.cc/150?img=6",
            timestamp: "2024-03-15T12:15:00.000Z"
          },
          {
            id: 3,
            content: "Mình học kanji này qua từ 交換 (trao đổi)",
            userId: "user7",
            username: "Quang Minh",
            userAvatar: "https://i.pravatar.cc/150?img=7",
            timestamp: "2024-03-15T13:00:00.000Z"
          },
          {
            id: 4,
            content: "Cách viết: Nét 1 là 亠, sau đó là chéo trái, chéo phải",
            userId: "user8",
            username: "Hồng Nhung",
            userAvatar: "https://i.pravatar.cc/150?img=8",
            timestamp: "2024-03-15T13:45:00.000Z"
          },
          {
            id: 5,
            content: "Kanji này cũng có trong từ 交わる (giao nhau, gặp gỡ)",
            userId: "user9",
            username: "Tuấn Kiệt",
            userAvatar: "https://i.pravatar.cc/150?img=9",
            timestamp: "2024-03-15T14:30:00.000Z"
          }
        ]
      },
      {
        id: 2,
        kanjiname: "Giao",
        kanji: "郊",
        reading: "コウ",
        kunyomi: "",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user6",
            username: "Thu Trang",
            userAvatar: "https://i.pravatar.cc/150?img=6",
            timestamp: "2024-03-15T12:15:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user7",
            username: "Quang Minh",
            userAvatar: "https://i.pravatar.cc/150?img=7",
            timestamp: "2024-03-15T13:00:00.000Z"
          }
        ]
      },
      {
        id: 3,
        kanjiname: "Giao",
        kanji: "蛍",
        reading: "コウ, キョウ",
        kunyomi: "みずち",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          },
          {
            id: 2,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user6",
            username: "Thu Trang",
            userAvatar: "https://i.pravatar.cc/150?img=6",
            timestamp: "2024-03-15T12:15:00.000Z"
          },
          {
            id: 3,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user7",
            username: "Quang Minh",
            userAvatar: "https://i.pravatar.cc/150?img=7",
            timestamp: "2024-03-15T13:00:00.000Z"
          }
        ]
      },
      {
        id: 4,
        kanjiname: "Giao",
        kanji: "鮫",
        reading: "コウ",
        kunyomi: "さめ, みずち",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          }
        ]
      },
      {
        id: 5,
        kanjiname: "Giao",
        kanji: "攪",
        reading: "カク, コウ",
        kunyomi: "みだす",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user5",
            username: "Đức Anh",
            userAvatar: "https://i.pravatar.cc/150?img=5",
            timestamp: "2024-03-15T11:30:00.000Z"
          }
        ]
      },
    ],
    grammar: [
      {
        id: 1,
        romaji: "tame ni",
        kana: "〜ために",
        meaning: "Vì",
        comments: [
          {
            id: 1,
            content: "Cấu trúc này dùng để chỉ mục đích của hành động",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          },
          {
            id: 2,
            content: "Ví dụ: 日本語を勉強するために、日本に行きます (Tôi đi Nhật để học tiếng Nhật)",
            userId: "user11",
            username: "Văn Hùng",
            userAvatar: "https://i.pravatar.cc/150?img=11",
            timestamp: "2024-03-15T16:00:00.000Z"
          },
          {
            id: 3,
            content: "Chú ý là động từ trước ために phải ở dạng nguyên thể",
            userId: "user12",
            username: "Lan Anh",
            userAvatar: "https://i.pravatar.cc/150?img=12",
            timestamp: "2024-03-15T16:45:00.000Z"
          },
          {
            id: 4,
            content: "Có thể dùng với danh từ: 健康のために運動します (Tập thể dục vì sức khỏe)",
            userId: "user13",
            username: "Thành Đạt",
            userAvatar: "https://i.pravatar.cc/150?img=13",
            timestamp: "2024-03-15T17:30:00.000Z"
          }
        ]
      },
      {
        id: 2,
        romaji: "aratameru",
        kana: "あらためる",
        meaning: "Lại",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 3,
        romaji: "tame",
        kana: "... がため",
        meaning: "Để ...",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 4,
        romaji: "tame",
        kana: "... ため",
        meaning: "Bởi, vì",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 5,
        romaji: "tameshiganai",
        kana: "ためしがない",
        meaning: "Chưa hề ...",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 6,
        romaji: "tameni",
        kana: "〜ために",
        meaning: "Để~, cho~, vì~",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 7,
        romaji: "tameshitemiru",
        kana: "ためしに…てみる",
        meaning: "Thử ... xem sao",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 8,
        romaji: "tameno",
        kana: "〜んがため（に）/〜んがための",
        meaning: "Với mục đích để~",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 9,
        romaji: "hitotsu ni wa tame de aru",
        kana: "ひとつには…ためである",
        meaning: "Một phần là do ...",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 10,
        romaji: "nowatameda",
        kana: "のは…ためだ",
        meaning: "Là vì, là để ...",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
    ],
    sentenceList: [
      {
        id: 1,
        sentence:
          "あなたの口座がある銀行が所有しないATM現金自動預け入れ支払機でキャッシュ・カードを使えば、使用者手数料を払わなければ...",
        translation:
          "Nếu  sử dụng thẻ tại ATM không thuộc ngân hàng của mình,  sẽ phải trả phí giao dịch...",
        comments: [
          {
            id: 1,
            content: "Câu này rất hữu ích khi đi Nhật du học",
            userId: "user14",
            username: "Minh Tuấn",
            userAvatar: "https://i.pravatar.cc/150?img=14",
            timestamp: "2024-03-15T18:15:00.000Z"
          },
          {
            id: 2,
            content: "所有 (しょゆう) là một từ quan trọng, có nghĩa là 'sở hữu'",
            userId: "user15",
            username: "Thùy Linh",
            userAvatar: "https://i.pravatar.cc/150?img=15",
            timestamp: "2024-03-15T19:00:00.000Z"
          },
          {
            id: 3,
            content: "Cấu trúc ば trong câu này diễn tả điều kiện",
            userId: "user16",
            username: "Đức Thắng",
            userAvatar: "https://i.pravatar.cc/150?img=16",
            timestamp: "2024-03-15T19:45:00.000Z"
          },
          {
            id: 4,
            content: "手数料 (てすうりょう) là một từ thường gặp ở ngân hàng",
            userId: "user17",
            username: "Hương Giang",
            userAvatar: "https://i.pravatar.cc/150?img=17",
            timestamp: "2024-03-15T20:30:00.000Z"
          },
          {
            id: 5,
            content: "Đây là một câu điều kiện phức tạp, nhưng rất thực tế",
            userId: "user18",
            username: "Quốc Bảo",
            userAvatar: "https://i.pravatar.cc/150?img=18",
            timestamp: "2024-03-15T21:15:00.000Z"
          }
        ]
      },
      {
        id: 2,
        sentence:
          "サム、あなたがいなくて毎日寂しい思いをしています。でも、私たちの置かれている状況を整理して、二人が一緒にいて両方が本当...",
        translation:
          "Sam à, mỗi ngày anh không có ở đây, em đều cảm thấy rất cô đơn. Nhưng chúng ta cần xem xét lại tình huống hiện tại để cả hai có thể thật sự bên nhau...",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 3,
        sentence:
          "あなたの次の職場は人手不足なので、最初から全力で仕事しなければならないだろう",
        translation:
          "Nơi làm việc tiếp theo của  đang thiếu nhân lực, nên  có lẽ sẽ phải làm hết sức mình ngay từ đầu.",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 4,
        sentence: "ルーシーの代わりに、ジェーンが今夜あなたのお世話をします",
        translation: "Tối nay Jane sẽ chăm sóc  thay cho Lucy.",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 5,
        sentence: "仮に私があなたの立場なら",
        translation: "Giả sử tôi ở trong vị trí của ...",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 6,
        sentence:
          "「タイの人々は地味な服装をしていますね」「あなたももしお寺巡りをしたいなら派手な服装は避けた方がいいですよ」",
        translation:
          "“Người Thái ăn mặc khá giản dị nhỉ.” “Nếu  cũng muốn tham quan chùa thì nên tránh mặc đồ sặc sỡ nhé.”",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 7,
        sentence: "あなたが思い切ってあの状況から脱してくれてうれしい。",
        translation:
          "Tôi thật sự mừng vì  đã dũng cảm thoát ra khỏi tình huống đó.",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 8,
        sentence: "あなたみたいにきれいな人に会ったのは初めてです",
        translation: "Đây là lần đầu tiên tôi gặp một người xinh đẹp như .",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 9,
        sentence:
          "会社であなたに会えなくなるのはとても寂しいけど、一年間海外で働くというあなたの夢が実現したこと là素晴らしいと思うわ。",
        translation:
          "Không gặp  ở công ty nữa thật sự khiến tôi rất buồn, nhưng tôi nghĩ thật tuyệt khi  đã thực hiện được ước mơ làm việc ở nước ngoài trong một năm.",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
      {
        id: 10,
        sentence:
          "あなたの家にいると、まるでふるさとに戻ったようにゆったりとくつろぐ",
        translation:
          "Khi ở nhà , tôi có cảm giác thư thái như thể mình đã trở về quê hương.",
        comments: [
          {
            id: 1,
            content: "Ngày Obon được tôn vinh cho các ông bố và ông nội",
            userId: "user10",
            username: "Phương Thảo",
            userAvatar: "https://i.pravatar.cc/150?img=10",
            timestamp: "2024-03-15T15:15:00.000Z"
          }
        ]
      },
    ],
    selectedWord: null,
    selectedKanji: null,
    selectedGrammar: null,
    selectedSentence: null
  }),
  mutations: {
    setSelectedWord(state, word) {
      state.selectedWord = word;
    },
    setSelectedKanji(state, kanji) {
      state.selectedKanji = kanji;
    },
    setSelectedGrammar(state, grammar) {
      state.selectedGrammar = grammar;
    },
    setSelectedSentence(state, sentence) {
      state.selectedSentence = sentence;
    },
    addComment(state, { type, itemId, comment }) {
      let item;
      switch (type) {
        case 'word':
          item = state.wordList.find(w => w.id === itemId);
          break;
        case 'kanji':
          item = state.KanjiList.find(k => k.id === itemId) || 
                state.KanjiList.find(k => k.kanji === itemId);
          break;
        case 'grammar':
          item = state.grammar.find(g => g.id === itemId);
          break;
        case 'sentence':
          item = state.sentenceList.find(s => s.id === itemId);
          break;
      }
      if (item) {
        if (!item.comments) {
          item.comments = [];
        }
        item.comments.unshift(comment);
      }
    }
  },
  actions: {
    getWordById({ state, commit }, id) {
      const word = state.wordList.find(w => w.id === id);
      commit('setSelectedWord', word);
      return word;
    },
    getKanjiById({ state, commit }, id) {
      const kanji = state.KanjiList.find(k => k.id === id);
      commit('setSelectedKanji', kanji);
      return kanji;
    },
    getKanjiByKanji({ state, commit }, kanjiChar) {
      const kanji = state.KanjiList.find(k => k.kanji === kanjiChar);
      commit('setSelectedKanji', kanji);
      return kanji;
    },
    getGrammarById({ state, commit }, id) {
      const grammar = state.grammar.find(g => g.id === id);
      commit('setSelectedGrammar', grammar);
      return grammar;
    },
    getSentenceById({ state, commit }, id) {
      const sentence = state.sentenceList.find(s => s.id === id);
      commit('setSelectedSentence', sentence);
      return sentence;
    },
    addComment({ commit }, { type, itemId, comment }) {
      commit('addComment', { type, itemId, comment });
    }
  },
  getters: {
    wordList: (state) => state.wordList,
    selectedWord: (state) => state.selectedWord,
    selectedKanji: (state) => state.selectedKanji,
    selectedGrammar: (state) => state.selectedGrammar,
    selectedSentence: (state) => state.selectedSentence,
    getCommentsByTypeAndId: (state) => (type, itemId) => {
      let item;
      switch (type) {
        case 'word':
          item = state.wordList.find(w => w.id === itemId);
          break;
        case 'kanji':
          item = state.KanjiList.find(k => k.id === itemId) || 
                state.KanjiList.find(k => k.kanji === itemId);
          break;
        case 'grammar':
          item = state.grammar.find(g => g.id === itemId);
          break;
        case 'sentence':
          item = state.sentenceList.find(s => s.id === itemId);
          break;
      }
      return item?.comments || [];
    },
    getRelatedKanjiList: (state) => (word) => {
      if (!word || !word.kanji) return [];
      
      // Extract individual kanji characters from the word's kanji field
      const kanjiChars = Array.from(word.kanji).filter(char => {
        // Check if the character is a kanji using Unicode ranges
        const code = char.charCodeAt(0);
        return (code >= 0x4E00 && code <= 0x9FFF) || // CJK Unified Ideographs
               (code >= 0x3400 && code <= 0x4DBF) || // CJK Unified Ideographs Extension A
               (code >= 0x20000 && code <= 0x2A6DF); // CJK Unified Ideographs Extension B
      });

      // Find kanji details for each character
      return kanjiChars.map(kanjiChar => {
        const kanjiDetail = state.KanjiList.find(k => k.kanji === kanjiChar);
        return kanjiDetail || {
          kanji: kanjiChar,
          reading: '',
          kunyomi: '',
          meaning: 'Chưa có thông tin'
        };
      });
    }
  }
};
