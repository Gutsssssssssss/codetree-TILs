import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;



public class Main {
	
	static Map<Integer, Integer> opp = new HashMap<Integer, Integer>();
	static int N, M, H, K, ret;
	static Set<int[]> runners;
	static Set<String> trees;
	static int ty, tx, td;
	static int[] dy = {-1, 0, 1, 0};
	static int[] dx = {0, 1, 0, -1};
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		opp.put(1, 3); opp.put(3, 1); opp.put(0, 2); opp.put(2, 0);
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		H = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		runners = new HashSet<int[]>();
		trees = new HashSet<String>();
		
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			runners.add(new int[] {
					Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken())
			});
		}
		
		for (int i = 0; i < H; i++) {
			st = new StringTokenizer(br.readLine());
			trees.add(st.nextToken() + "," + st.nextToken());
		}
		
		int md = (N + 2) / 2;
		ty = md; tx = md; td = 0;
		
		int mxCnt = 1;
		int cnt = 0;
		int flag = 0;
		int val = 1;
		// K 턴
		for (int turn = 1; turn <= K; turn++) {
			// [1] 도망자 이동
			for (int[] runner : runners) {
				int ry = runner[0];
				int rx = runner[1];
				int rd = runner[2];
				
				if ((Math.abs(ry - ty) + Math.abs(rx - tx)) <= 3) {
					int ny = ry + dy[rd];
					int nx = rx + dx[rd];
					if (ny >= 1 && ny <= N && nx >=1 && nx <= N) { // 격자 벗어나지 않음
						if (ny == ty && nx == tx) continue;
						ry = ny;
						rx = nx;
					} else { // 격자를 벗어남.
						rd = opp.get(rd);
						ny = ry + dy[rd];
						nx = rx + dx[rd];
						if (ny == ty && nx == tx) continue;
						ry = ny;
						rx = nx;
					}
					runner[0] = ny;
					runner[1] = nx;
					runner[2] = rd;
				}
				
			}
			
			// [2] 술래 이동
			ty = ty + dy[td];
			tx = tx + dx[td];
			cnt++;
			
			if (ty == 1 && tx == 1) {
				flag = 1;
				cnt = 1;
				mxCnt = N;
				val = -1;
				td = opp.get(td);
			} else if (ty == md && tx == md) {
				flag = 0;
				cnt = 0;
				mxCnt = 1;
				val = 1;
				td = opp.get(td);
			} else {
				if (mxCnt == cnt) {
					if (flag == 0) {
						flag = 1;
						td = (td + val + 4) % 4;
						cnt = 0;
					} else {
						flag = 0;
						td = (td + val + 4) % 4;
						mxCnt += val;
						cnt = 0;
					}
				}
			}
			
			// [3] 잡기
			for (int[] bound : new HashSet<int[]>(Arrays.asList(
					new int[] {ty, tx}, new int[] {ty + dy[td], tx + dx[td]},
					new int[] {ty + 2 * dy[td], tx + 2 * dx[td]}))) {
				int by = bound[0];
				int bx = bound[1];
				if (trees.contains(by+","+bx)) continue;
				
				for (int[] runner : runners) {
					int ry = runner[0];
					int rx = runner[1];
					if (ry == by && rx == bx) {
						ret += turn;
					}
				}
			}
		}
		System.out.println(ret);
	}
} // class
