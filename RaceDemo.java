/**
 * เครื่องมือทดลองที่ 1 — ไฟล์นี้ให้มาแล้ว ไม่ต้องแก้
 *
 * รันการทดลองเดิมซ้ำ 20 รอบ แล้วพิมพ์ตารางผลออกมา
 * ให้นิสิตคัดลอกตารางนี้ไปใส่ในรายงาน
 * 
 *
 * วิธีรัน:  java RaceDemo
 */
public class RaceDemo {

    private static final int ROUNDS = 20;
    private static final int THREADS = 4;
    private static final int OPS_PER_THREAD = 50000;
    private static final int EXPECTED = THREADS * OPS_PER_THREAD;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Demo 1: Lost Update (deposit) ===");
        System.out.println(THREADS + " threads, each depositing 1 unit "
                + OPS_PER_THREAD + " times");
        System.out.println("The correct total is " + EXPECTED + " in every round\n");

        System.out.println(" round  |   actual total  | expected |   lost");
        System.out.println("--------+-----------------+----------+---------");

        int wrongRounds = 0;
        int worst = 0;

        for (int round = 1; round <= ROUNDS; round++) {
            int actual = runOneRound();
            int lost = EXPECTED - actual;

            if (lost != 0) {
                wrongRounds++;
            }
            if (lost > worst) {
                worst = lost;
            }

            System.out.printf("  %2d    |   %,11d   |  %,6d  |  %,6d%s%n",
                    round, actual, EXPECTED, lost, (lost == 0 ? "" : "   <-- WRONG"));
        }

        System.out.println();
        System.out.println("=== Experiment summary ===");
        System.out.println("Rounds with a wrong total : " + wrongRounds + " / " + ROUNDS);
        System.out.println("Largest amount lost       : " + worst);
        System.out.println();

        if (wrongRounds == 0) {
            System.out.println("Every round produced the correct total.");
            System.out.println("If you have not fixed Account.java yet, this machine");
            System.out.println("simply never interleaved the threads badly enough.");
            System.out.println("Raise THREADS or OPS_PER_THREAD and run it again,");
            System.out.println("or run in interpreted mode:  java -Xint RaceDemo");
        } else {
            System.out.println("Same program, same code, different answer each round.");
            System.out.println("That is a race condition. Continue in README.md");
        }
    }

    /** ทำการทดลองหนึ่งรอบ แล้วคืนยอดสุดท้ายที่ได้ */
    private static int runOneRound() throws InterruptedException {
        final Account acc = new Account(1, 0);
        Thread[] workers = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            workers[i] = new Thread(new Runnable() {
                public void run() {
                    for (int k = 0; k < OPS_PER_THREAD; k++) {
                        acc.deposit(1);
                    }
                }
            });
        }

        for (int i = 0; i < THREADS; i++) {
            workers[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            workers[i].join();
        }

        return acc.balance();
    }
}
