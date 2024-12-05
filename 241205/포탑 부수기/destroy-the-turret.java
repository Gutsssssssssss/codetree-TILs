import java.io.*;
import java.util.*;

public class Main {
    static int N, M, K;
    static int[][] arr;
    static int[][] turn; // 공격한 턴수를 기록(최근 공격 체크)

    static class Position {
        int x, y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static boolean bfs(int si, int sj, int ei, int ej) {
        Queue<Position> q = new LinkedList<>();
        Position[][] v = new Position[N][M]; // 경로를 표시하기 위한 visited

        q.add(new Position(si, sj));
        v[si][sj] = new Position(si, sj);
        int d = arr[si][sj]; // damage

        while (!q.isEmpty()) {
            Position cur = q.poll();
            int ci = cur.x, cj = cur.y;
            if (ci == ei && cj == ej) { // 목적지 좌표 도달
                arr[ei][ej] = Math.max(0, arr[ei][ej] - d); // 목표 d만큼 타격
                while (true) {
                    Position prev = v[ci][cj];
                    if (prev.x == si && prev.y == sj) // 시작(공격자)까지 되집어 왔으면 종료
                        return true;
                    arr[prev.x][prev.y] = Math.max(0, arr[prev.x][prev.y] - d / 2);
                    ci = prev.x;
                    cj = prev.y;
                }
            }

            // 우선순위: 우/하/좌/상 (미방문, 조건: >0 포탑있고)
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int ni = (ci + dir[0] + N) % N;
                int nj = (cj + dir[1] + M) % M;
                if (v[ni][nj] == null && arr[ni][nj] > 0) {
                    q.add(new Position(ni, nj));
                    v[ni][nj] = new Position(ci, cj);
                }
            }
        }
        return false;
    }

    static void bomb(int si, int sj, int ei, int ej, Set<Position> fset) {
        int d = arr[si][sj]; // damage
        arr[ei][ej] = Math.max(0, arr[ei][ej] - d); // 목표 d만큼 타격
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] dir : directions) {
            int ni = (ei + dir[0] + N) % N;
            int nj = (ej + dir[1] + M) % M;
            if (!(ni == si && nj == sj)) {
                arr[ni][nj] = Math.max(0, arr[ni][nj] - d / 2);
                fset.add(new Position(ni, nj));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        arr = new int[N][M];
        turn = new int[N][M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int T = 1; T <= K; T++) {
            // [1] 공격자 선정
            int mn = 5001, mxTurn = 0, si = -1, sj = -1;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (arr[i][j] <= 0) continue;
                    if (mn > arr[i][j] || (mn == arr[i][j] && mxTurn < turn[i][j]) ||
                        (mn == arr[i][j] && mxTurn == turn[i][j] && si + sj < i + j) ||
                        (mn == arr[i][j] && mxTurn == turn[i][j] && si + sj == i + j && sj < j)) {
                        mn = arr[i][j];
                        mxTurn = turn[i][j];
                        si = i;
                        sj = j;
                    }
                }
            }

            // [2] 공격
            int mx = 0, mnTurn = T, ei = N, ej = M;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (arr[i][j] <= 0) continue;
                    if (mx < arr[i][j] || (mx == arr[i][j] && mnTurn > turn[i][j]) ||
                        (mx == arr[i][j] && mnTurn == turn[i][j] && ei + ej > i + j) ||
                        (mx == arr[i][j] && mnTurn == turn[i][j] && ei + ej == i + j && ej > j)) {
                        mx = arr[i][j];
                        mnTurn = turn[i][j];
                        ei = i;
                        ej = j;
                    }
                }
            }

            arr[si][sj] += (N + M);
            turn[si][sj] = T;
            Set<Position> fset = new HashSet<>();
            fset.add(new Position(si, sj));
            fset.add(new Position(ei, ej));

            if (!bfs(si, sj, ei, ej)) {
                bomb(si, sj, ei, ej, fset);
            }

            // [3] 포탑 정비
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (arr[i][j] > 0 && !fset.contains(new Position(i, j))) {
                        arr[i][j]++;
                    }
                }
            }

            int cnt = 0;
            for (int[] row : arr) {
                for (int cell : row) {
                    if (cell > 0) cnt++;
                }
            }
            if (cnt <= 1) break;
        }

        int maxVal = 0;
        for (int[] row : arr) {
            for (int cell : row) {
                maxVal = Math.max(maxVal, cell);
            }
        }
        System.out.println(maxVal);
    }
}