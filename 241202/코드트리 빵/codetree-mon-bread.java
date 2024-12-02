import java.io.*;
import java.util.*;

public class Main {
    static int N, M;
    static int[][] arr;
    static Set<int[]> basecamp = new HashSet<>();
    static Map<Integer, int[]> store = new HashMap<>();
    static int[] di = {-1, 1, 0, 0}; // 방향: 상, 하, 좌, 우
    static int[] dj = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        arr = new int[N + 2][N + 2];
        for (int i = 0; i < N + 2; i++) {
            Arrays.fill(arr[i], 1); // 경계를 1로 초기화
        }

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 1; j <= N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
                if (arr[i][j] == 1) {
                    basecamp.add(new int[]{i, j}); // Basecamp 좌표 추가
                    arr[i][j] = 0;
                }
            }
        }

        for (int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());
            int i = Integer.parseInt(st.nextToken());
            int j = Integer.parseInt(st.nextToken());
            store.put(m, new int[]{i, j});
        }

        int result = solve();
        System.out.println(result);
    }

    static int[] find(int si, int sj, Set<int[]> dests) {
        Queue<int[]> q = new LinkedList<>();
        boolean[][] visited = new boolean[N + 2][N + 2];
        List<int[]> candidates = new ArrayList<>();

        q.add(new int[]{si, sj});
        visited[si][sj] = true;

        while (!q.isEmpty()) {
            Queue<int[]> nextQueue = new LinkedList<>();
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int ci = cur[0];
                int cj = cur[1];

                if (dests.contains(new int[]{ci, cj})) {
                    candidates.add(new int[]{ci, cj});
                } else {
                    for (int d = 0; d < 4; d++) {
                        int ni = ci + di[d];
                        int nj = cj + dj[d];
                        if (!visited[ni][nj] && arr[ni][nj] == 0) {
                            nextQueue.add(new int[]{ni, nj});
                            visited[ni][nj] = true;
                        }
                    }
                }
            }

            if (!candidates.isEmpty()) {
                candidates.sort(Comparator.comparingInt(a -> a[0] * 1000 + a[1])); // 행/열 우선순위 정렬
                return candidates.get(0);
            }
            q = nextQueue;
        }

        return new int[]{-1, -1}; // 실패 시
    }

    static int solve() {
        Queue<int[]> q = new LinkedList<>();
        int time = 1;
        int[] arrived = new int[M + 1];

        while (!q.isEmpty() || time <= M) {
            Queue<int[]> nextQueue = new LinkedList<>();
            List<int[]> arrivedList = new ArrayList<>();

            // [1] 편의점 방향으로 이동
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                int ci = cur[0];
                int cj = cur[1];
                int m = cur[2];

                if (arrived[m] == 0) {
                    int[] dest = find(store.get(m)[0], store.get(m)[1], new HashSet<>(
                            Arrays.asList(
                                    new int[]{ci - 1, cj}, new int[]{ci + 1, cj},
                                    new int[]{ci, cj - 1}, new int[]{ci, cj + 1}
                            )
                    ));
                    if (Arrays.equals(dest, store.get(m))) {
                        arrived[m] = time;
                        arrivedList.add(dest);
                    } else {
                        nextQueue.add(new int[]{dest[0], dest[1], m});
                    }
                }
            }
            q = nextQueue;

            // [2] 도착 처리
            for (int[] pos : arrivedList) {
                arr[pos[0]][pos[1]] = 1; // 이동 불가 처리
            }

            // [3] 시간 번호 멤버의 Basecamp 선택
            if (time <= M) {
                int[] storePos = store.get(time);
                int[] base = find(storePos[0], storePos[1], basecamp);
                basecamp.remove(base);
                arr[base[0]][base[1]] = 1;
                q.add(new int[]{base[0], base[1], time});
            }

            time++;
        }

        return Arrays.stream(arrived).max().orElse(0);
    }
}