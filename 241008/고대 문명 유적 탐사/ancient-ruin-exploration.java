import java.util.*;

public class Main {
    static int[][] rotate(int[][] arr, int si, int sj) { // 90도 시계방향 회전
        int[][] narr = new int[5][5];
        for (int i = 0; i < 5; i++) {
            narr[i] = arr[i].clone();
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                narr[si + i][sj + j] = arr[si + 3 - j - 1][sj + i];
            }
        }
        return narr;
    }

    static int bfs(int[][] arr, int[][] v, int si, int sj, int clr) {
        Queue<int[]> q = new LinkedList<>();
        Set<String> sset = new HashSet<>();
        int cnt = 0;

        q.add(new int[]{si, sj});
        v[si][sj] = 1;
        sset.add(si + "," + sj);
        cnt++;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int ci = cur[0], cj = cur[1];

            // 네방향, 범위내, 미방문, 조건: 같은 값이면
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int ni = ci + dir[0], nj = cj + dir[1];
                if (0 <= ni && ni < 5 && 0 <= nj && nj < 5 && v[ni][nj] == 0 && arr[ci][cj] == arr[ni][nj]) {
                    q.add(new int[]{ni, nj});
                    v[ni][nj] = 1;
                    sset.add(ni + "," + nj);
                    cnt++;
                }
            }
        }

        if (cnt >= 3) { // 유물이면: cnt 리턴 + clr==1이면 0으로 clear
            if (clr == 1) { // 0으로 초기화
                for (String pos : sset) {
                    String[] parts = pos.split(",");
                    int i = Integer.parseInt(parts[0]);
                    int j = Integer.parseInt(parts[1]);
                    arr[i][j] = 0;
                }
            }
            return cnt;
        } else { // 3개 미만이면 0리턴
            return 0;
        }
    }

    static int countClear(int[][] arr, int clr) { // clr==1인 경우 3개이상값들을 0으로 clear
        int[][] v = new int[5][5];
        int cnt = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) { // 미방문인 경우 같은 값이면 fill
                if (v[i][j] == 0) {
                    int t = bfs(arr, v, i, j, clr);
                    cnt += t;
                }
            }
        }
        return cnt;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // 입력받기
        int K = sc.nextInt();
        int M = sc.nextInt();
        int[][] arr = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                arr[i][j] = sc.nextInt();
            }
        }
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < M; i++) {
            lst.add(sc.nextInt());
        }
        
        List<Integer> ans = new ArrayList<>();

        for (int turn = 0; turn < K; turn++) { // K턴을 진행(유물이 없는 경우 즉시종료)
            // [1] 탐사진행
            int mxCnt = 0;
            int[][] marr = new int[5][5];

            for (int rot = 1; rot < 4; rot++) { // 회전수->열->행 (작은순)
                for (int sj = 0; sj < 3; sj++) {
                    for (int si = 0; si < 3; si++) {
                        // rot 회수만큼 90도 시계방향 회전 => narr
                        int[][] narr = new int[5][5];
                        for (int i = 0; i < 5; i++) {
                            narr[i] = arr[i].clone();
                        }

                        for (int r = 0; r < rot; r++) {
                            narr = rotate(narr, si, sj);
                        }

                        // 유무개수 카운트
                        int t = countClear(narr, 0);
                        if (mxCnt < t) { // 최대개수
                            mxCnt = t;
                            marr = narr;
                        }
                    }
                }
            }

            // 유물이 없는 경우 턴 즉시종료
            if (mxCnt == 0) {
                break;
            }

            // [2] 연쇄획득
            int cnt = 0;
            arr = marr;
            while (true) {
                int t = countClear(arr, 1);
                if (t == 0) {
                    break; // 연쇄획득 종료 => 다음 턴으로..
                }
                cnt += t; // 획득한 유물 개수 누적

                // arr의 0값인 부분 리스트에서 순서대로 추가
                for (int j = 0; j < 5; j++) {
                    for (int i = 4; i >= 0; i--) {
                        if (arr[i][j] == 0 && !lst.isEmpty()) {
                            arr[i][j] = lst.remove(0);
                        }
                    }
                }
            }

            ans.add(cnt); // 이번턴 연쇄획득한 개수 추가
        }

        // 결과 출력
        for (int a : ans) {
            System.out.print(a + " ");
        }
    }
}