
import java.io.*;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class Main {
    static int N, M;
    static int[][] arr;
    static Set<int[]> basecamp = new HashSet<>();
    static Map<Integer, int[]> store = new HashMap<>();
    static int[] di = {-1, 1, 0, 0};
    static int[] dj = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        arr = new int[N + 2][N + 2];
        for (int i = 0; i < N + 2; i++) {
            Arrays.fill(arr[i], 1); // Initialize borders as walls
        }

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
                if (arr[i][j] == 1) {
                    basecamp.add(new int[]{i, j});
                    arr[i][j] = 0;
                }
            }
        }

        for (int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());
            int si = Integer.parseInt(st.nextToken());
            int sj = Integer.parseInt(st.nextToken());
            store.put(m, new int[]{si, sj});
        }

        System.out.println(solve());
    }

    static int[] find(int si, int sj, Set<int[]> dests) {
        Queue<int[]> q = new LinkedList<>();
        boolean[][] visited = new boolean[N + 2][N + 2];
        q.add(new int[]{si, sj, 0}); // {x, y, depth}
        visited[si][sj] = true;

        List<int[]> candidates = new ArrayList<>();

        while (!q.isEmpty()) {
            int[] current = q.poll();
            int ci = current[0], cj = current[1], depth = current[2];

            // 목적지 확인
            if (contains(dests, ci, cj)) {
                candidates.add(new int[]{ci, cj});
            }

            // 네 방향 탐색
            for (int d = 0; d < 4; d++) {
                int ni = ci + di[d];
                int nj = cj + dj[d];

                if (!visited[ni][nj] && arr[ni][nj] == 0) {
                    q.add(new int[]{ni, nj, depth + 1}); // 큐에 추가
                    visited[ni][nj] = true;
                }
            }

            // 목적지를 발견한 경우, 같은 Depth에서의 탐색만 허용
            if (!candidates.isEmpty() && !q.isEmpty() && q.peek()[2] > depth) {
                candidates.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
                return candidates.get(0);
            }
        }

        return null;
    }

    static int solve() {
        Queue<int[]> q = new LinkedList<>();
        int time = 1;
        int[] arrived = new int[M + 1];
        Arrays.fill(arrived, 0);

        while (!q.isEmpty() || time == 1) {
            Queue<int[]> nextQueue = new LinkedList<>();
            List<int[]> arrivedList = new ArrayList<>();

            for (int[] person : q) {
                int ci = person[0], cj = person[1], m = person[2];
                if (arrived[m] == 0) {
                    int[] nextMove = find(store.get(m)[0], store.get(m)[1],
                            new HashSet<>(Arrays.asList(
                                    new int[]{ci - 1, cj}, new int[]{ci + 1, cj}, new int[]{ci, cj - 1}, new int[]{ci, cj + 1}
                            )));
                    if (Arrays.equals(nextMove, store.get(m))) {
                        arrived[m] = time;
                        arrivedList.add(nextMove);
                    } else {
                        nextQueue.add(new int[]{nextMove[0], nextMove[1], m});
                    }
                }
            }

            q = nextQueue;

            for (int[] pos : arrivedList) {
                arr[pos[0]][pos[1]] = 1;
            }

            if (time <= M) {
                int[] storeLocation = store.get(time);
                int[] basecampLocation = find(storeLocation[0], storeLocation[1], basecamp);
                basecamp.remove(basecampLocation);
                arr[basecampLocation[0]][basecampLocation[1]] = 1;
                q.add(new int[]{basecampLocation[0], basecampLocation[1], time});
            }

            time++;
        }

        return Arrays.stream(arrived).max().getAsInt();
    }

    static boolean contains(Set<int[]> set, int x, int y) {
        for (int[] arr : set) {
            if (arr[0] == x && arr[1] == y) return true;
        }
        return false;
    }
}