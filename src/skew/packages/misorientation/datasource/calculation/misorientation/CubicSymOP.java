package skew.packages.misorientation.datasource.calculation.misorientation;
/**
 * CubicSymOP defines the 24 symmetrical operators for a cubic structure
 * @author Jinhui Qin, 2011
 *
 */
 
public class CubicSymOP {
	 private int num;
	 private float [][][]op;
	 public CubicSymOP(){
		 this.num=24;
		 this.op = new float[24][3][3];
		 
		 set(this.op[0],1, 0, 0,0, 1, 0, 0, 0, 1);
				 
		  
		  set(this.op[1],1, 0, 0,0, -1, 0, 0, 0, -1);
		  
		  
		  set(this.op[2],1, 0, 0,0, 0, -1, 0, 1, 0);
		  
		  
		  set(this.op[3],1, 0, 0,0, 0, 1, 0, -1, 0);
		   
		 
		  set(this.op[4],-1, 0, 0,0, 1, 0, 0, 0, -1);
		   
		  
		  set(this.op[5],-1, 0, 0,0, -1, 0, 0, 0, 1);
		   
		  
		  set(this.op[6],-1, 0, 0,0, 0, -1, 0, -1, 0);
		   
		 
		  set(this.op[7],-1, 0, 0,0, 0, 1, 0, 1, 0);
		   
		   
		  set(this.op[8],0, 1, 0,-1, 0, 0, 0, 0, 1);
		   
		   
		  set(this.op[9],0, 1, 0,0, 0, -1, -1, 0, 0);
		   
		 
		  set(this.op[10],0, 1, 0,1, 0, 0, 0, 0, -1);
		   
		   
		  set(this.op[11],0, 1, 0,0, 0, 1, 1, 0, 0);
		   
		   
		  set(this.op[12],0, -1, 0,1, 0, 0, 0, 0, 1);
		   
		   
		  set(this.op[13],0, -1, 0,0, 0, -1, 1, 0, 0);
		   
		 
		  set(this.op[14],0, -1, 0,-1, 0, 0, 0, 0, -1);
		   
		  
		  set(this.op[15],0, -1, 0,0, 0, 1, -1, 0, 0);
		   
		  
		  set(this.op[16],0, 0, 1,0, 1, 0, -1, 0, 0);
		   
		   
		  set(this.op[17],0, 0, 1,1, 0, 0, 0, 1, 0);
		   
		   
		  set(this.op[18],0, 0, 1,0, -1, 0, 1, 0, 0);
		   
		   
		  set(this.op[10],0, 0, 1,-1, 0, 0, 0, -1, 0);
		   
		  
		  set(this.op[20],0, 0, -1,0, 1, 0, 1, 0, 0);
		   
		  
		  set(this.op[21],0, 0, -1,-1, 0, 0, 0, 1, 0);
		   
		   
		  set(this.op[22],0, 0, -1,0, -1, 0, -1, 0, 0);
		   
		   
		  set(this.op[23],0, 0, -1,1, 0, 0, 0, -1, 0);
		   
	 }
	private void set(float[][] is, int i, int j, int k, int l, int m, int n,
			int o, int p, int q) {
		 
		is[0][0]=i;
		is[0][1]=j;
		is[0][2]=k;
		is[1][0]=l;
		is[1][1]=m;
		is[1][2]=n;
		is[2][0]=o;
		is[2][1]=p;
		is[2][2]=q;
		 
		
		
	}
	public int getNumOP(){return this.num;}
	public float[][] getOP(int i){return this.op[i];}

}
