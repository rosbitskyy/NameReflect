/* Copyright 2012 Rosbitskyy Ruslan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package namereflect.trunk.java.namereflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Ruslan Rosbitskyy
 * Date: 08.07.14 15:07
 * Project: test-case
 * Developed by  R.Rosbitskyy
 */
public class NameReflect {

    public final static int UANAZYVNYI = 0; // кто що
    public final static int UARODOVYI = 1; // кого чого
    public final static int UADAVALNYI = 2; // кому чому
    public final static int UAZNAHIDNYI = 3; // кого що
    public final static int UAORUDNYI = 4; // ким чим
    public final static int UAMISZEVYI = 5; // на кому на чому
    public final static int UAKLYCHNYI = 6; // кого що

    /**
     * Список гласных украинского языка
     *
     * @var string
     */
    private String vowels = "аеиоуіїєюя";
    /**
     * Список согласных украинского языка
     *
     * @var string
     */
    private String consonant = "бвгджзйклмнпрстфхцчшщ";
    /**
     * Українські шиплячі приголосні
     *
     * @var string
     */
    private String shyplyachi = "жчшщ";
    /**
     * Українські нешиплячі приголосні
     *
     * @var string
     */
    private String neshyplyachi = "бвгдзклмнпрстфхц";
    /**
     * Українські завжди м’які звуки
     *
     * @var string
     */
    private String myaki = "ьюяєї";
    /**
     * Українські губні звуки
     *
     * @var string
     */
    private String gubni = "мвпбф";

    /**
     * Массив содержит елементы типа Word. Это все слова которые нужно обработать и просклонять
     */
    private List<Word> words = new ArrayList<Word>();

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    /**
     * Переменная, в которую заносится слово с которым сейчас идет работа
     */
    protected String workingWord = "";

    public NameReflect setWorkingWord(String workingWord) {
        this.workingWord = workingWord;
        return this;
    }

    // пол класса
    private int gender = 0;

    public int getGender() {
        return gender;
    }

    public NameReflect setGender(int gender) {
        this.gender = gender;
        return this;
    }

    public NameReflect(String surname, String firstname, String middlename, Boolean isMan) {
        this.words.add(new Word(surname)
                .setGender(isMan ? Word.MAN : Word.WOMAN)
                .setType(Word.TYPE_SURNAME)
        );
        this.words.add(new Word(firstname)
                .setGender(isMan ? Word.MAN : Word.WOMAN)
                .setType(Word.TYPE_FIRSTNAME)
        );
        this.words.add(new Word(middlename)
                .setGender(isMan ? Word.MAN : Word.WOMAN)
                .setType(Word.TYPE_FATHERNAME)
        );
        this.setGender(isMan ? Word.MAN : Word.WOMAN);

        this.wordCases();
    }

    public NameReflect(String surname, String firstname, String middlename) {
        this.words.add(new Word(surname)
                .setType(Word.TYPE_SURNAME)
        );
        this.words.add(new Word(firstname)
                .setType(Word.TYPE_FIRSTNAME)
        );
        this.words.add(new Word(middlename)
                .setType(Word.TYPE_FATHERNAME)
        );
        for (Word word : this.getWords()) {
            this.detectSex(word);
        }
        this.wordCases();
    }

    /**
     * Конструктор з автоматичним визначенням статі особи та типу частин ПІБ
     *
     * @param nameParts - ПІБ, чи будьяка комбінація частин призвища чі то ім'я чи побатькові
     *                  (наприклад Розбицький Руслан)
     */
    public NameReflect(String nameParts) {
        if (nameParts == null || nameParts.length() == 0)
            nameParts = "Розбицький Руслан Станіславович";
        String[] names = nameParts.split(" ");
        int man = 0, wo = 0;
        int fatherGender = 0;
        for (String name : names) {
            Word tmpName = new Word(name);
            detectWordType(tmpName);
            if (tmpName.getType() == Word.TYPE_FATHERNAME) fatherGender = tmpName.getGender();
            detectSex(tmpName);
            this.getWords().add(tmpName);
            man += tmpName.getGender() == Word.MAN ? 1 : 0;
            wo += tmpName.getGender() == Word.WOMAN ? 1 : 0;
        }
        if (fatherGender > 0) this.setGender(fatherGender);
        else if (man > wo) this.setGender(Word.MAN);
        else this.setGender(Word.WOMAN);

        this.wordCases();
    }

    // вырезаем последние символы слова сконца
    protected String last(int length) {
        //Сколько букв нужно вырезать
        return this.workingWord.substring(this.workingWord.length() - length);
    }

    // вырезаем последние символы слова сконца, затем предпоследние с начала результата
    protected String last(int length, int last) {
        //Сколько букв нужно вырезать все или только часть
        if (length > this.workingWord.length()) return this.workingWord;
        String src = this.workingWord.substring(this.workingWord.length() - length);
        if (src.length() < last) return src;
        return src.substring(0, last);
    }

    /**
     * Чергування українських приголосних
     * Чергування г к х —» з ц с
     *
     * @param letter літера, яку необхідно перевірити на чергування
     * @return string літера, де вже відбулося чергування
     */
    private String inverseGKH(String letter) {
        if (letter.equals("г")) return "з";
        if (letter.equals("к")) return "ц";
        if (letter.equals("х")) return "с";
        return letter;
    }

    /**
     * Чергування українських приголосних
     * Чергування г к —» ж ч
     *
     * @param letter літера, яку необхідно перевірити на чергування
     * @return string літера, де вже відбулося чергування
     */
    private String inverse2(String letter) {
        if (letter.equals("к")) return "ч";
        if (letter.equals("г")) return "ж";
        return letter;
    }

    private NameReflect detectSex(Word word) {
        if (word.getType() == 0) return this;
        switch (word.getType()) {
            case Word.TYPE_FIRSTNAME:
                return genderByFirstName(word);
            case Word.TYPE_SURNAME:
                return genderBySurName(word);
            case Word.TYPE_FATHERNAME:
                return genderByFatherName(word);
        }
        return this;
    }

    /**
     * Визначення статі, за правилами по-батькові
     *
     * @param word об’єкт класу зі словом, для якого необхідно визначити стать
     */
    protected NameReflect genderByFatherName(Word word) {
        this.setWorkingWord(word.getWord());

        if (this.last(2).equals("ич")) {
            word.setGender(Word.MAN); // мужчина
        } else if (this.last(2).equals("на")) {
            word.setGender(Word.WOMAN); // женщина
        }
        return this;
    }

    /**
     * Визначення статі, за правилами прізвища
     *
     * @param word об’єкт класу зі словом, для якого необхідно визначити стать
     */
    protected NameReflect genderBySurName(Word word) {
        this.setWorkingWord(word.getWord());

        float man = 0; //Мужчина
        float woman = 0; //Женщина

        String[] ends = new String[]{"ов", "ин", "ев", "єв", "ін", "їн", "ий", "їв", "ів", "ой", "ей"};
        if (Arrays.asList(ends).contains(this.last(2))) {
            man += 0.4;
        }

        if (Arrays.asList(new String[]{"ова", "ина", "ева", "єва", "іна"}).contains(this.last(3))) {
            woman += 0.4;
        }

        if ("ая".contains(this.last(2))) {
            woman += 0.4;
        }

        word.setGender((man > woman) ? Word.MAN : Word.WOMAN);
        return this;
    }

    /**
     * Визначення статі, за правилами імені
     *
     * @param word об’єкт класу зі словом, для якого необхідно визначити стать
     */
    protected NameReflect genderByFirstName(Word word) {
        this.setWorkingWord(word.getWord());

        float man = 0; //Мужчина
        float woman = 0; //Женщина
        //Попробуем выжать максимум из имени
        //Если имя заканчивается на й, то скорее всего мужчина
        if (this.last(1).equals("й")) {
            man += 0.9;
        }

        if (this.inNames(this.workingWord, new String[]{"Петро", "Микола"})) {
            man += 30;
        }

        if (Arrays.asList(new String[]{"он", "ов", "ав", "ам", "ол", "ан", "рд", "мп", "ко", "ло", "ро"})
                .contains(this.last(2))) {
            man += 0.5;
        }

        if (Arrays.asList(new String[]{"бов", "нка", "яра", "ила", "опа"})
                .contains(this.last(3))) {
            woman += 0.5;
        }

        if (this.consonant.contains(this.last(1))) {
            man += 0.01;
        }

        if (this.last(1).equals("ь")) {
            man += 0.02;
        }

        if ("дь".equals(this.last(2))) {
            woman += 0.1;
        }

        if (Arrays.asList(new String[]{"ель", "бов"})
                .contains(this.last(3))) {
            woman += 0.4;
        }

        word.setGender((man > woman) ? Word.MAN : Word.WOMAN);
        return this;
    }

    /**
     * Функция проверяет, входит ли имя nameNeed в перечень имен names.
     *
     * @param nameNeed - имя которое нужно найти
     * @param names    - перечень имен в котором нужно найти имя
     */
    protected boolean inNames(String nameNeed, String[] names) {
        for (String name : names) {
            if (nameNeed.toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ідентифікує слово визначаючи чи це ім’я, чи це прізвище, чи це побатькові
     * - 1 - ім’я Word.TYPE_FIRSTNAME
     * - 2 - прізвище Word.TYPE_SURNAME
     * - 3 - по-батькові Word.TYPE_FATHERNAME
     *
     * @param word об’єкт класу зі словом, яке необхідно ідентифікувати
     */
    protected NameReflect detectWordType(Word word) {
        String namepart = word.getWord();
        this.setWorkingWord(namepart);

        //Считаем вероятность
        float first = 0;
        float second = 0;
        float father = 0;

        //если смахивает на отчество
        if (Arrays.asList(new String[]{"вна", "чна", "ліч"}).contains(this.last(3)) ||
                Arrays.asList(new String[]{"ьмич", "ович"}).contains(this.last(4))) {
            father += 3;
        }

        //Похоже на имя
        if ("тин".equals(this.last(3))
                || Arrays.asList(new String[]{"ьмич", "юбов", "івна", "явка", "орив", "кіян"})
                .contains(this.last(4))) {
            first += 0.5;
        }

        //Исключения
        if (this.inNames(namepart,
                new String[]{"Лев", "Гаїна", "Афіна", "Антоніна", "Ангеліна", "Альвіна",
                        "Альбіна", "Аліна", "Павло", "Олесь", "Микола", "Мая", "Англеліна", "Елькін"})) {
            first += 10;
        }

        //похоже на фамилию
        if (Arrays.asList(new String[]{"ов", "ін", "ев", "єв", "ий", "ин", "ой", "ко", "ук", "як", "ца",
                "их", "ик", "ун", "ок", "ша", "ая", "га", "єк", "аш", "ив", "юк",
                "ус", "це", "ак", "бр", "яр", "іл", "ів", "ич", "сь", "ей", "нс",
                "яс", "ер", "ай", "ян", "ах", "ць", "ющ", "іс", "ач", "уб", "ох",
                "юх", "ут", "ча", "ул", "вк", "зь", "уц", "їн", "де", "уз"}).contains(this.last(2))) {
            second += 0.4;
        }

        if (Arrays.asList(new String[]{"ова", "ева", "єва", "тих", "рик", "вач", "аха", "шен", "мей", "арь", "вка",
                "шир", "бан", "чий", "іна", "їна", "ька", "ань", "ива", "аль", "ура", "ран",
                "ало", "ола", "кур", "оба", "оль", "нта", "зій", "ґан", "іло", "шта", "юпа",
                "рна", "бла", "еїн", "има", "мар", "кар", "оха", "чур", "ниш", "ета", "тна",
                "зур", "нір", "йма", "орж", "рба", "іла", "лас", "дід", "роз", "аба"})
                .contains(this.last(3))
                || Arrays.asList(new String[]{"лест", "мара", "обка", "рока", "сика",
                "одна", "нчар", "вата", "ндар", "грій"})
                .contains(this.last(4))) {
            second += 0.4;
        }

        if (Arrays.asList(new String[]{"ьник", "нчук", "тник", "кирь", "ский", "шена", "шина", "вина",
                "нина", "гана", "гана", "хній", "зюба", "орош", "орон", "сило", "руба"})
                .contains(this.last(4))) {
            second += 0.4;
        }

        if (this.last(1).equals("і")) {
            second += 0.2;
        }

        float max = Math.max(first, second);
        max = Math.max(max, father);

        if (first == max) {
            word.setType(Word.TYPE_FIRSTNAME);
        } else if (second == max) {
            word.setType(Word.TYPE_SURNAME);
        } else {
            word.setType(Word.TYPE_FATHERNAME);
        }

        return this;
    }

    /**
     * Визначення групи для іменників 2-ї відміни
     * 1 - тверда
     * 2 - мішана
     * 3 - м’яка
     * <p/>
     * Правило:
     * - Іменники з основою на твердий нешиплячий належать до твердої групи:
     * береза, дорога, Дніпро, шлях, віз, село, яблуко.
     * - Іменники з основою на твердий шиплячий належать до мішаної групи:
     * пожеж-а, пущ-а, тиш-а, алич-а, вуж, кущ, плющ, ключ, плече, прізвище.
     * - Іменники з основою на будь-який м'який чи пом'якше­ний належать до м'якої групи:
     * земля [земл'а], зоря [зор'а], армія [арм'ійа], сім'я [с'імйа], серпень, фахівець,
     * трамвай, су­зір'я [суз'ірйа], насіння [насін'н'а], узвишшя Іузвиш'ш'а
     *
     * @param word іменник, групу якого необхідно визначити
     * @return int номер групи іменника
     */
    private int detect2ndWithdrawal(String word) {
        String osnova = word;
        List<String> stack = new ArrayList<String>();
        //Ріжемо слово поки не зустрінемо приголосний і записуемо в стек всі голосні які зустріли
        while ((this.vowels + 'ь').contains(osnova.substring(osnova.length() - 1))) {
            stack.add(osnova.substring(osnova.length() - 1));
            osnova = osnova.substring(0, osnova.length() - 1);
        }
        String last = "0"; //нульове закінчення
        if (stack.size() > 0) {
            last = stack.get(stack.size() - 1);
        }

        String osnovaEnd = osnova.substring(osnova.length() - 1);
        if (this.neshyplyachi.contains(osnovaEnd) && !this.myaki.contains(last)) {
            return 1;
        } else if (this.shyplyachi.contains(osnovaEnd) && !this.myaki.contains(last)) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Шукаємо в слові word перше входження літери з переліку vowels з кінця
     *
     * @param word   слово, якому необхідно знайти голосні
     * @param vowels перелік літер, які треба знайти
     * @return string ( 1 ) перша з кінця літера з переліку vowels
     */
    private String firstLastVowel(String word, String vowels) {
        int length = word.length();
        for (int i = length - 1; i > 0; i--) {
            String c = word.substring(i, 1);
            if (vowels.lastIndexOf(c) > -1) {
                return c;
            }
        }
        return "";
    }

    /**
     * Пошук основи іменника word
     * Основа слова - це частина слова (як правило незмінна), яка вказує на його лексичне значення.
     *
     * @param word слово, в якому необхідно знати основу
     * @return string основа іменника word
     */
    private String getOsnova(String word) {
        String osnova = word;
        //Ріжемо слово поки не зустрінемо приголосний
        while ((this.vowels + 'ь').lastIndexOf(osnova.substring(osnova.length() - 1)) > -1) {
            osnova = osnova.substring(0, osnova.length() - 1);
        }
        return osnova;
    }

    /**
     * Возвращает результаты склонения как список
     *
     * @return List<String> - список склонений
     */
    public List<String> getCaseList() {
        List<String> result = new ArrayList<String>();
        for (Word word : this.getWords()) {
            int i = 0;
            for (String entry : word.getNameCases()) {
                if (i >= result.size()) {
                    result.add(entry);
                } else {
                    result.set(i, result.get(i) + " " + entry);
                }
                i++;
            }

        }
        return result;
    }

    public String getCaseList(int uaVidminokId) {
        String res = "";
        List<String> list = getCaseList();
        res = list.get(uaVidminokId);
        return res;
    }

    /**
     * Производит склонение всех слов, который хранятся в массиве this.words
     */
    protected NameReflect wordCases() {

        for (Word word : this.getWords()) {
            this.wordCase(word);
        }

        return this;
    }

    /**
     * Склоняет слово word по нужным правилам в зависимости от пола и типа слова
     *
     * @param word слово, которое нужно просклонять
     */
    private NameReflect wordCase(Word word) {
        this.setWorkingWord(word.getWord());
        List<String> result = null;
        if (word.getType() == Word.TYPE_FIRSTNAME) {
            result = firstName(word.getGender());
        } else if (word.getType() == Word.TYPE_SURNAME) {
            result = surName(word.getGender());
        } else if (word.getType() == Word.TYPE_FATHERNAME) {
            result = fatherName(word.getGender());
        }
        if (result != null) {
            word.setNameCases(result);
        } else {
            List<String> s = new ArrayList<String>();
            for (int i = 0; i < 7; i++) s.add(word.getWord());
            word.setNameCases(s);
        }
        return this;
    }

    /**
     * Функція намагається застосувати ланцюг правил для імен
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    private List<String> firstName(int gender) {
        if (gender == Word.MAN) return this.rulesChain(gender, new int[]{1, 2, 3});
        else return this.rulesChain(gender, new int[]{1, 2});
    }

    /**
     * Функція намагається застосувати ланцюг правил для прізвищ
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    protected List<String> surName(int gender) {
        if (gender == Word.MAN) return this.rulesChain(gender, new int[]{5, 1, 2, 3, 4});
        else return this.rulesChain(gender, new int[]{3, 1});
    }

    /**
     * Фунція відмінює по-батькові
     *
     * @return List<String> nameCases - якщо слово успішно змінене, null - якщо невдалося провідміняти слово
     */
    protected List<String> fatherName(int gender) {
        if (gender == Word.MAN) {
            if (Arrays.asList(new String[]{"ич", "іч"}).contains(this.last(2))) {
                return this.wordForms(this.workingWord, new String[]{"а", "у", "а", "ем", "у", "у"});
            }
        } else {
            if ("вна".equals(this.last(3))) {
                return this.wordForms(this.workingWord, new String[]{"и", "і", "у", "ою", "і", "о"}, 1);
            }
        }
        return null;
    }

    /**
     * Над текущим словом (this.workingWord) выполняются правила в порядке
     * указаном в rulesArray.
     * gender служит для указания какие правила использовать
     * мужские или женские
     *
     * @param gender     - префикс мужских/женских правил
     * @param rulesArray - массив, порядок выполнения правил
     * @return boolean если правило было задествовано, тогда true, если нет - тогда false
     */
    protected List<String> rulesChain(int gender, int[] rulesArray) {
        List<String> res = null;
        for (int ruleID : rulesArray) {
            if (gender == Word.MAN) {
                switch (ruleID) {
                    case 1:
                        res = manRule1();
                        break;
                    case 2:
                        res = manRule2();
                        break;
                    case 3:
                        res = manRule3();
                        break;
                    case 4:
                        res = manRule4();
                        break;
                    case 5:
                        res = manRule5();
                        break;
                }
            } else {
                switch (ruleID) {
                    case 1:
                        res = womanRule1();
                        break;
                    case 2:
                        res = womanRule2();
                        break;
                    case 3:
                        res = womanRule3();
                        break;
                }
            }
            if (res != null) return res;
        }
        return res;
    }

    /**
     * Українські чоловічі та жіночі імена, що в називному відмінку однини закінчуються на -а (-я),
     * відмінються як відповідні іменники І відміни.
     * <p/>
     * - Примітка 1. Кінцеві приголосні основи г, к, х у жіночих іменах
     * у давальному та місцевому відмінках однини перед закінченням -і
     * змінюються на з, ц, с: Ольга - Ользі, Палажка - Палажці, Солоха - Солосі.</li>
     * - Примітка 2. У жіночих іменах типу Одарка, Параска в родовому відмінку множини
     * в кінці основи між приголосними з"являється звук о: Одарок, Парасок. </li>
     *
     * @return List<String> - якщо було задіяно правило з переліку, null - якщо правило не знайдено
     */
    protected List<String> manRule1() {
        //Предпоследний символ
        String beforeLast = this.last(2, 1);

        //Останні літера або а
        if (this.last(1).equals("а")) {
            String[] xyu = new String[]{
                    beforeLast + "и",
                    this.inverseGKH(beforeLast) + "і",
                    beforeLast + "у",
                    beforeLast + "ою",
                    this.inverseGKH(beforeLast) + "і",
                    beforeLast + "о"
            };
            return this.wordForms(this.workingWord, xyu, 2);
        }
        //Остання літера я
        else if (this.last(1).equals("я")) {
            //Перед останньою літерою стоїть я
            if (beforeLast.equals("і")) {
                return this.wordForms(this.workingWord, new String[]{"ї", "ї", "ю", "єю", "ї", "є"}, 1);
            } else {
                return this.wordForms(this.workingWord, new String[]{
                        beforeLast + "і",
                        this.inverseGKH(beforeLast) + "і",
                        beforeLast + "ю",
                        beforeLast + "ею",
                        this.inverseGKH(beforeLast) + "і",
                        beforeLast + "е"
                }, 2);
            }
        }
        return null;
    }

    /**
     * Імена, що в називному відмінку закінчуються на -р, у родовому мають закінчення -а:
     * Віктор - Віктора, Макар - Макара, але: Ігор - Ігоря, Лазар - Лазаря.
     *
     * @return List<String> - якщо було задіяно правило з переліку, null - якщо правило не знайдено
     */
    protected List<String> manRule2() {
        if (this.last(1).equals("р")) {
            if (this.inNames(this.workingWord, new String[]{"Ігор", "Лазар"})) {
                return this.wordForms(this.workingWord, new String[]{"я", "еві", "я", "ем", "еві", "е"}, 0);
            } else {
                String osnova = this.workingWord;
                if (osnova.substring(osnova.length() - 2, 1).equals("і")) {
                    osnova = osnova.substring(0, osnova.length() - 2) + "о" + osnova.substring(osnova.length() - 1, 1);
                }
                return this.wordForms(osnova, new String[]{"а", "ові", "а", "ом", "ові", "е"}, 0);
            }
        }
        return null;
    }

    /**
     * Українські чоловічі імена, що в називному відмінку однини закінчуються на приголосний та -о,
     * відмінюються як відповідні іменники ІІ відміни.
     *
     * @return List<String> - якщо було задіяно правило з переліку, null - якщо правило не знайдено
     */
    protected List<String> manRule3() {
        //Предпоследний символ
        String beforeLast = this.last(2, 1);

        if ((this.consonant + "оь").contains(this.last(1))) {
            int group = this.detect2ndWithdrawal(this.workingWord);
            String osnova = this.getOsnova(this.workingWord);
            //В іменах типу Антін, Нестір, Нечипір, Прокіп, Сидір, Тиміш, Федір голосний і виступає тільки в
            //називному відмінку, у непрямих - о: Антона, Антонові
            //Чергування і -» о всередині
            String osLast = osnova.substring(osnova.length() - 1);
            if (!osLast.equals("й") && osnova.substring(osnova.length() - 2, osnova.length() - 2).equals("і")
                    && !(Arrays.asList(new String[]{"світ", "цвіт"})
                    .contains(osnova.substring(osnova.length() - 4, 4)))
                    && !this.inNames(this.workingWord, new String[]{"Гліб"})) {
                osnova = osnova.substring(0, osnova.length() - 2)
                        + "о" + osnova.substring(osnova.length() - 1);
            }

            //Випадання букви е при відмінюванні слів типу Орел
            if (osnova.substring(0, 1).equals("о")
                    && this.firstLastVowel(osnova, this.vowels + "гк").equals("е")
                    && this.last(2).equals("сь")) {
                int delim = osnova.indexOf("е");
                osnova = osnova.substring(0, delim) +
                        osnova.substring(delim + 1, osnova.length() - delim);
            }


            if (group == 1) {
                //Тверда група
                //Слова що закінчуються на ок
                if (this.last(2).equals("ок") && !this.last(3).equals("оок")) {
                    return this.wordForms(this.workingWord, new String[]{"ка", "кові", "ка", "ком", "кові", "че"}, 2);
                }
                //Російські прізвища на ов, ев, єв
                else if (Arrays.asList(new String[]{"ов", "ев", "єв"}).contains(this.last(2))
                        && !this.inNames(this.workingWord, new String[]{"Лев", "Остромов"})) {
                    return this.wordForms(osnova, new String[]{osLast + "а", osLast + "у", osLast + "а", osLast + "им",
                            osLast + "у", this.inverse2(osLast) + "е"}, 1);
                }
                //Російські прізвища на ін
                else if ("ін".equals(this.last(2))) {
                    return this.wordForms(this.workingWord, new String[]{"а", "у", "а", "ом", "у", "е"});
                } else {
                    return this.wordForms(osnova, new String[]{osLast + "а", osLast + "ові", osLast + "а",
                            osLast + "ом", osLast + "ові", this.inverse2(osLast) + "е"}, 1);
                }
            }
            if (group == 2) {
                //Мішана група
                return this.wordForms(osnova, new String[]{"а", "еві", "а", "ем", "еві", "е"});
            }
            if (group == 3) {
                //М’яка група
                //Соловей
                if (this.last(2).equals("ей") && this.gubni.contains(this.last(3, 1))) {
                    osnova = this.workingWord.substring(0, this.workingWord.length() - 2) + "’";
                    return this.wordForms(osnova, new String[]{"я", "єві", "я", "єм", "єві", "ю"});
                } else if (this.last(1).equals("й") || beforeLast.equals("і")) {
                    return this.wordForms(this.workingWord, new String[]{"я", "єві", "я", "єм", "єві", "ю"}, 1);
                }
                //Швець
                else if (this.workingWord.equals("швець")) {
                    return this.wordForms(this.workingWord, new String[]{"евця", "евцеві", "евця",
                            "евцем", "евцеві", "евцю"}, 4);
                }
                //Слова що закінчуються на ець
                else if (this.last(3).equals("ець")) {
                    return this.wordForms(this.workingWord, new String[]{"ця", "цеві", "ця", "цем", "цеві", "цю"}, 3);
                }
                //Слова що закінчуються на єць яць
                else if (Arrays.asList(new String[]{"єць", "яць"}).contains(this.last(3))) {
                    return this.wordForms(this.workingWord, new String[]{"йця", "йцеві", "йця",
                            "йцем", "йцеві", "йцю"}, 3);
                } else {
                    return this.wordForms(osnova, new String[]{"я", "еві", "я", "ем", "еві", "ю"});
                }
            }
        }
        return null;
    }

    /**
     * Якщо слово закінчується на і, то відмінюємо як множину
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    protected List<String> manRule4() {
        if (this.last(1).equals("і")) {
            return this.wordForms(this.workingWord, new String[]{"их", "им", "их", "ими", "их", "і"}, 1);
        }
        return null;
    }

    /**
     * Якщо слово закінчується на ий або ой
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    protected List<String> manRule5() {
        if (Arrays.asList(new String[]{"ий", "ой"}).contains(this.last(2))) {
            return this.wordForms(this.workingWord, new String[]{"ого", "ому", "ого", "им", "ому", "ий"}, 2);
        }
        return null;
    }

    /**
     * Українські чоловічі та жіночі імена, що в називному відмінку однини закінчуються на -а (-я),
     * відмінються як відповідні іменники І відміни.
     * - Примітка 1. Кінцеві приголосні основи г, к, х у жіночих іменах
     * у давальному та місцевому відмінках однини перед закінченням -і
     * змінюються на з, ц, с: Ольга - Ользі, Палажка - Палажці, Солоха - Солосі.
     * - Примітка 2. У жіночих іменах типу Одарка, Параска в родовому відмінку множини
     * в кінці основи між приголосними з"являється звук о: Одарок, Парасок
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    protected List<String> womanRule1() {
        //Предпоследний символ
        String beforeLast = this.last(2, 1);

        //Якщо закінчується на ніга -» нога
        if (this.last(4).equals("ніга")) {
            String osnova = this.workingWord.substring(0, this.workingWord.length() - 3) + "о";
            return this.wordForms(osnova, new String[]{"ги", "зі", "гу", "гою", "зі", "го"});
        }

        //Останні літера або а
        else if (this.last(1).equals("а")) {
            return this.wordForms(this.workingWord, new String[]{
                    beforeLast + "и",
                    this.inverseGKH(beforeLast) + "і",
                    beforeLast + "у", beforeLast + "ою",
                    this.inverseGKH(beforeLast) + "і",
                    beforeLast + "о"}
                    , 2);
        }
        //Остання літера я
        else if (this.last(1).equals("я")) {
            if (this.vowels.contains(beforeLast)) {
                return this.wordForms(this.workingWord, new String[]{"ї", "ї", "ю", "єю", "ї", "є"}, 1);
            } else {
                return this.wordForms(this.workingWord, new String[]{
                        beforeLast + "і",
                        this.inverseGKH(beforeLast) + "і",
                        beforeLast + "ю", beforeLast + "ею",
                        this.inverseGKH(beforeLast) + "і", beforeLast + "е"}
                        , 2);
            }
        }
        return null;
    }

    /**
     * Українські жіночі імена, що в називному відмінку однини закінчуються на приголосний,
     * відмінюються як відповідні іменники ІІІ відміни
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    protected List<String> womanRule2() {
        if ((this.consonant + "ь").contains(this.last(1))) {
            String osnova = this.getOsnova(this.workingWord);
            String apostrof = "";
            String duplicate = "";
            String osLast = osnova.substring(osnova.length() - 1, 1);
            String osBeforeLast = osnova.substring(osnova.length() - 2, 1);

            //Чи треба ставити апостроф
            if ("мвпбф".contains(osLast) && this.vowels.contains(osBeforeLast)) {
                apostrof = "’";
            }

            //Чи треба подвоювати
            if ("дтзсцлн".contains(osLast)) {
                duplicate = osLast;
            }

            //Відмінюємо
            if (this.last(1).equals("ь")) {
                return this.wordForms(osnova, new String[]{"і", "і", "ь", duplicate + apostrof + "ю", "і", "е"});
            } else {
                return this.wordForms(osnova, new String[]{"і", "і", "", duplicate + apostrof + "ю", "і", "е"});
            }
        }
        return null;
    }

    /**
     * Якщо слово на ськ або це російське прізвище
     *
     * @return boolean true - якщо було задіяно правило з переліку, false - якщо правило не знайдено
     */
    protected List<String> womanRule3() {
        //Предпоследний символ
        String beforeLast = this.last(2, 1);

        //Донская
        if (this.last(2).equals("ая")) {
            return this.wordForms(this.workingWord, new String[]{"ої", "ій", "ую", "ою", "ій", "ая"}, 2);
        }

        //Ті що на ськ
        if (this.last(1).equals("а")
                && Arrays.asList(new String[]{"ов", "ев", "єв", "ив", "ьк", "тн", "рн", "ин"})
                .contains(this.last(3, 2))) {
            return this.wordForms(this.workingWord, new String[]{
                    beforeLast + "ої",
                    beforeLast + "ій", beforeLast + "у",
                    beforeLast + "ою", beforeLast + "ій", beforeLast + "о"}
                    , 2);
        }

        return null;
    }

    /*
     * Склоняет слово word, удаляя из него replaceLast последних букв
     * и добавляя в каждый падеж окончание из массива endings.
     *
     * @param word    слово, к которому нужно добавить окончания
     * @param endings массив окончаний
     * @param replaceLast сколько последних букв нужно убрать с начального слова
     */
    protected List<String> wordForms(String word, String[] endings) {
        return wordForms(word, endings, 0);
    }

    protected List<String> wordForms(String word, String[] endings, int replaceLast) {
        //Создаем массив с именительный падежом
        List<String> result = new ArrayList<String>();
        result.add(this.workingWord);
        //Убираем в окончание лишние буквы
        if (replaceLast > 0 && replaceLast < word.length()) {
            word = word.substring(0, word.length() - replaceLast);
        }

        //Добавляем окончания
        for (int padegIndex = 1; padegIndex < 7; padegIndex++) {
            result.add(word + endings[padegIndex - 1]);
        }

        return result;
    }
}
