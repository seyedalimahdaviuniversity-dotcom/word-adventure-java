import java.util.*; 
// ایمپورت کردن تمام کلاس‌های مورد نیاز مثل Scanner، List، ArrayList، Random و ...

/* ===================== MAIN ===================== */
public class Main {
    public static void main(String[] args) {
        // نقطه‌ی شروع برنامه
        // یک شیء از GameEngine می‌سازیم که مغز اصلی بازی است
        GameEngine game = new GameEngine();

        // اجرای بازی
        game.start();
    }
}

/* ===================== GAME ENGINE ===================== */
class GameEngine {

    // برای گرفتن ورودی از کاربر
    private Scanner scanner = new Scanner(System.in);

    // بازیکن بازی
    private Player player;

    // لیستی از چالش‌ها (به‌خاطر Polymorphism)
    private List<Challenge> challenges = new ArrayList<>();

    public void start() {

        // گرفتن اسم بازیکن
        System.out.print("Enter your name: ");
        player = new Player(scanner.nextLine());

        // اضافه کردن چالش‌ها به بازی
        // اینجا چندریختی داریم چون همه Challenge هستند
        challenges.add(new GuessWordChallenge());
        challenges.add(new FixSentenceChallenge());
        challenges.add(new GuessWordChallenge());

        // خوش‌آمدگویی
        System.out.println("\nWelcome " + player.getName() + "!\n");

        // اجرای مرحله به مرحله‌ی چالش‌ها
        for (Challenge challenge : challenges) {

            // اگر جون بازیکن تموم شد، بازی قطع می‌شود
            if (player.getLives() <= 0) break;

            // Scanner را به چالش می‌دهیم
            challenge.setScanner(scanner);

            // اجرای چالش
            boolean result = challenge.play();

            // اگر کاربر درست جواب داد
            if (result) {
                player.addScore(challenge.getScore());
                System.out.println("Correct! Score: " + player.getScore());
            }
            // اگر اشتباه جواب داد
            else {
                player.loseLife();
                System.out.println("Wrong! Lives left: " + player.getLives());
            }

            // جداکننده‌ی ظاهری
            System.out.println("----------------------------------");
        }

        // پایان بازی
        System.out.println("Game Over");
        System.out.println("Final Score: " + player.getScore());
    }
}

/* ===================== PLAYER ===================== */
class Player {

    // اسم بازیکن
    private String name;

    // امتیاز
    private int score;

    // تعداد جون
    private int lives;

    // سازنده‌ی کلاس Player
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.lives = 3;
    }

    // متدهای getter برای دسترسی امن (Encapsulation)
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getLives() { return lives; }

    // اضافه کردن امتیاز
    public void addScore(int value) { score += value; }

    // کم کردن جون
    public void loseLife() { lives--; }
}

/* ===================== ABSTRACT CHALLENGE ===================== */
abstract class Challenge {

    // Scanner که از بیرون به چالش داده می‌شود
    protected Scanner scanner;

    // امتیاز هر چالش
    protected int score = 10;

    // ست کردن Scanner
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    // گرفتن امتیاز
    public int getScore() {
        return score;
    }

    // متدی که هر چالش باید پیاده‌سازی کند
    public abstract boolean play();
}

/* ===================== GUESS WORD CHALLENGE ===================== */
class GuessWordChallenge extends Challenge {

    // کلمه‌ای که باید حدس زده شود
    private String word;

    // حروفی که کاربر تا الان حدس زده
    private Set<Character> guessedLetters = new HashSet<>();

    // سازنده‌ی چالش حدس کلمه
    public GuessWordChallenge() {

        // لیست کلمات
        String[] words = {"java", "object", "string", "class"};

        // انتخاب تصادفی یک کلمه
        word = words[new Random().nextInt(words.length)];
    }

    @Override
    public boolean play() {

        // تعداد تلاش‌ها
        int attempts = 5;

        // تا وقتی تلاش داریم
        while (attempts > 0) {

            // نمایش کلمه به صورت مخفی
            System.out.println("Word: " + maskedWord());

            // گرفتن حرف از کاربر
            System.out.print("Guess a letter: ");
            String input = scanner.nextLine().toLowerCase();

            // اگر بیشتر از یک حرف وارد شد
            if (input.length() != 1) {
                System.out.println("Enter one character only.");
                continue;
            }

            // گرفتن حرف
            char c = input.charAt(0);

            // اضافه کردن به لیست حدس‌ها
            guessedLetters.add(c);

            // اگر حرف داخل کلمه نبود
            if (!word.contains(String.valueOf(c))) {
                attempts--;
            }

            // اگر کل کلمه حدس زده شد
            if (maskedWord().equals(word)) {
                return true;
            }
        }

        // اگر باخت
        System.out.println("The word was: " + word);
        return false;
    }

    // ساخت نسخه‌ی مخفی کلمه
    private String maskedWord() {
        StringBuilder sb = new StringBuilder();

        // بررسی تک‌تک حروف
        for (char c : word.toCharArray()) {
            sb.append(guessedLetters.contains(c) ? c : '_');
        }

        return sb.toString();
    }
}

/* ===================== FIX SENTENCE CHALLENGE ===================== */
class FixSentenceChallenge extends Challenge {

    // جمله‌ی صحیح
    private String correct = "Java is a powerful language.";

    @Override
    public boolean play() {

        // جمله‌ی خراب
        String broken = "java  is a POWERFUL   language";

        // نمایش به کاربر
        System.out.println("Fix this sentence:");
        System.out.println(broken);
        System.out.print("Your answer: ");

        // گرفتن ورودی
        String userInput = scanner.nextLine().trim();

        // مقایسه بعد از نرمال‌سازی
        return normalize(userInput).equals(normalize(correct));
    }

    // یکسان‌سازی رشته برای مقایسه
    private String normalize(String s) {
        return s.trim()                  // حذف فاصله‌های اول و آخر
                .replaceAll("\\s+", " ") // یکی کردن فاصله‌ها
                .toLowerCase();          // تبدیل به حروف کوچک
    }
}
