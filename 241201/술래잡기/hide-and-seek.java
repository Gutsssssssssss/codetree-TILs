

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Main {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		int N = Integer.parseInt(st.nextToken());
		int MM = Integer.parseInt(st.nextToken());
		int H = Integer.parseInt(st.nextToken());
		int K = Integer.parseInt(st.nextToken());
		
		ArrayList<int[]> arr = new ArrayList<int[]>();
		for(int i=0; i<MM; i++) {
			st = new StringTokenizer(br.readLine());
			arr.add(new int[] {
					Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken())
			});
		}
		
		Set<String> tree = new HashSet<>();
		for(int i=0; i<H; i++) {
			st = new StringTokenizer(br.readLine());
			String y = st.nextToken();
			String x = st.nextToken();
			tree.add(y + "," + x);
		}
		
		int[] dy = {0, 0, 1, -1};
		int[] dx = {-1, 1, 0, 0};
		Map<Integer, Integer> opp = new HashMap<>();
        opp.put(0, 1);
        opp.put(1, 0);
        opp.put(2, 3);
        opp.put(3, 2);
		
		int[] tdy = {-1, 0, 1, 0};
		int[] tdx = {0, 1, 0, -1};
		
		int mxCnt = 1;
		int cnt = 0;
		int flag = 0;
		int val = 1;
		
		int M = (N+1) / 2;
		int ty = M;
		int tx = M;
		int td = 0;
		
		int ans = 0;
		for(int k=1; k<=K; k++) {
			// [1] 도망자의 이동
			for(int i=0; i<arr.size(); i++) {
				int[] runner = arr.get(i);
				if(Math.abs(runner[0] - ty) + Math.abs(runner[1] - tx)<=3) {
					int d = runner[2];
					int ny = runner[0] + dy[d];
					int nx = runner[1] + dx[d];
					if((1<=ny && ny<=N) && (1<=nx && nx<=N)) { // 술래 체크 이동
						if(ny==ty && nx == tx) continue;
					} else {
						d = opp.get(d);
						ny = runner[0] + dy[d];
						nx = runner[1] + dx[d];
						if(ny==ty && nx == tx) continue;
					}
					runner[0] = ny;
					runner[1] = nx;
					runner[2] = d;
				}
			}
			// [2] 술래의 이동
			cnt += 1;
			ty = ty + tdy[td];
			tx = tx + tdx[td];
			if(ty == 1 && tx == 1) {
				mxCnt = N;
				cnt = 1;
				flag = 1;
				val = -1;
				td = 2;
			} else if(ty == M && tx == M) {
				mxCnt = 1;
				cnt = 0;
				flag = 0;
				val = 1;
				td = 0;
			} else {
				if(cnt == mxCnt) {
					cnt = 0;
					td = (td+val+4) % 4;
					if(flag==0) {
						flag = 1;
					} else {
						flag = 0;
						mxCnt += val;
					}
				}
			}
			// [3] 잡기
			Set<String> tset = new HashSet<String>();
			tset.add(ty+","+tx);
			tset.add((ty+tdy[td])+","+(tx+tdx[td]));
			tset.add((ty+tdy[td]*2)+","+(tx+tdx[td]*2));
			
			for(int i=arr.size()-1; i>=0; i--) {
				int[] runner = arr.get(i);
				String pos = runner[0] + "," + runner[1];
				if(tset.contains(pos) && !tree.contains(pos)) {
					arr.remove(i);
					ans += k;
				}
			}
			
			if(arr.isEmpty()) {
				break;
			}
		}
		System.out.println(ans);
	} // main
} // class
