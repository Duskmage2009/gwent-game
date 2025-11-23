# Gwent Deck Statistics Generator

Консольна програма для аналізу колод Gwent і формування статистики по різних атрибутах карт.


Проект заснований на карточній грі **Gwent** зі всесвіту The Witcher і працює з двома основними сутностями:

### 1. Deck (Колода)
- `name` - назва колоди
- `faction` - фракція колоди (Northern Realms, Monsters, Nilfgaard, Skellige, Scoia'tael, Syndicate, Neutral)
- `leaderAbility` - назва здібності лідера
- `provisionLimit` - ліміт провізії колоди
- **`categories`** - категорії (Control, Tempo, Midrange тощо)
- `cards` - список карт у колоді

### 2. Card (Карта) 
- `name` - назва карти
- `provision` - вартість провізії
- `power` - сила карти
- `type` - тип карти (Unit, Special, Artifact, Stratagem)
- `faction` - фракція карти

##  Моя підтримувана статистика

Програма може формувати статистику по наступних атрибутах:

| Атрибут | Опис | Приклад використання |
|---------|------|---------------------|
| `faction` | Підрахунок карт по фракціях | Скільки карт Northern Realms, Monsters тощо |
| `type` | Підрахунок карт по типах | Скільки Unit, Special, Artifact, Stratagem |
| `provision` | Підрахунок карт по вартості провізії | Скільки карт коштує 4, 5, 6 провізії тощо |
| `power` | Підрахунок Unit-карт по силі | Скільки загонів мають силу 3, 4, 5 тощо |
| `leaderAbility` | Підрахунок колод по здібностях лідера | Скільки колод використовують кожну здібність |
| **`categories`** | **Підрахунок по категоріях** | **Скільки разів зустрічається Control, Tempo тощо** |
| `totalPower` | Підрахунок колод по загальній силі загонів | Групування колод по діапазонах сили (0-50, 51-100 тощо) |
| `deckFaction` | Підрахунок колод по фракціях | Скільки колод кожної фракції |

##  Приклади файлів

### Приклад вхідного JSON-файлу (northern_realms_deck.json):
```json
{
  "name": "Northern Realms Starter Deck",
  "faction": "Northern Realms",
  "leaderAbility": "Pincer Maneuver",
  "provisionLimit": 150,
  "categories": "Control, Tempo, Midrange",
  "cards": [
    {
      "name": "Prince Anseis",
      "provision": 10,
      "power": 6,
      "type": "Unit",
      "faction": "Northern Realms"
    },
    {
      "name": "Reinforcements",
      "provision": 6,
      "power": 0,
      "type": "Special",
      "faction": "Northern Realms"
    },
    {
      "name": "Dandelion: Poet",
      "provision": 9,
      "power": 4,
      "type": "Unit",
      "faction": "Neutral"
    }
  ]
}
```

### Приклад вихідного XML-файлу
```xml
<?xml version="1.0" encoding="UTF-8"?>
<statistics>
  <item>
    <value>Tempo</value>
    <count>25</count>
  </item>
  <item>
    <value>Control</value>
    <count>18</count>
  </item>
  <item>
    <value>Midrange</value>
    <count>12</count>
  </item>
  <item>
    <value>Swarm</value>
    <count>10</count>
  </item>
</statistics>
```

##  Збірка та запуск


### Збірка проекту
```bash
mvn clean package
```

Після збірки в директорії `target/` з'явиться файл `gwent-game-1.0.0.jar`

### Запуск
```bash
java -jar target/gwent-game-1.0.0.jar <directory_path> <attribute> [thread_count]
```

**Параметри:**
- `directory_path` - шлях до директорії з JSON-файлами
- `attribute` - атрибут для статистики
- `thread_count` - кількість потоків (опціонально, за замовчуванням я зробив 4)

### Приклади використання
```bash
# Статистика по фракціях карт
java -jar target/gwent-game-1.0.0.jar ./decks faction

# Статистика по типах карт з 8 потоками
java -jar target/gwent-game-1.0.0.jar ./decks type 8

# Статистика по категоріях (текстове поле з кількома значеннями)
java -jar target/gwent-game-1.0.0.jar ./decks categories

# Статистика по загальній силі колод
java -jar target/gwent-game-1.0.0.jar ./decks totalPower

# Статистика по здібностях лідерів
java -jar target/gwent-game-1.0.0.jar ./decks leaderAbility 4
```

##  Експерименти з продуктивністю

Тестування проводилося на наборі з 100 JSON-файлів, що містять загалом 2,500 колод (~62,500 карт).

### Результати:

| Кількість потоків | Час виконання (мс) | Прискорення | Ефективність |
|--------------------|--------------------  |-------------|--------------|
| 1                  | 2,847                | 1.00x       | 100%         |
| 2                  | 1,523                | 1.87x       | 93%          |
| 4                  | 856                  | 3.33x       | 83%          |
| 8                  | 734                  | 3.88x       | 48%          |
| 16                 | 698                  | 4.08x       | 25%          |

### Висновки:

1. **Оптимальна кількість потоків: 4-8** 
   - Дає найкращий баланс між продуктивністю та використанням ресурсів
   - При 4 потоках досягається ~3.3x прискорення
   
2. **Diminishing Returns**
   - Збільшення з 8 до 16 потоків дає приріст лише 5% (~36ms)
   - Overhead на управління потоками починає перевищувати вигоду

3. **Рекомендації по використанню:**
   - Для невеликих наборів (< 10 файлів): 1-2 потоки
   - Для середніх наборів (10-100 файлів): 4 потоки
   - Для великих наборів (100+ файлів): 8 потоків

4. **Streaming підхід**
   - Використання Jackson для потокової обробки дозволяє парсити великі файли без повного завантаження в пам'ять
   - Memory footprint залишається стабільним незалежно від розміру файлів

