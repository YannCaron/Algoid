/*
2d graphic calculator
created by Vrushal Nanavati the 03-12-2013
*/

set inc =(5/10);
set p;
set q;
set m=0;
set n=0;
set right=algo.getWidth();
set left=-right;
set bottom=algo.getHeight();
set top=-bottom;
set  interval=2;
set scale=10;
set scale1=1;
algo.setStroke(0.6);
algo.setStack (1000);

set getformula=function()
{
	 text.output("Ex : (x*x)+(y*y)-1600(donot write =0)\nEnter the values 10 time grater to get exact curve");
	  return text.inputText("Eq : ");
};
set init=function()
{       algo.setColor(3);
	algo.goTo(left,0);
	algo.lineTo(right,0);
	algo.goTo(0,bottom);
	algo.lineTo(0,top);
	 for(set i=0;i<=right;i+=scale)
	   {
		algo.goTo(i,0);
		algo.lineTo(i,-interval);
		algo.goTo(-i,0);
		algo.lineTo(-i,-interval);
		     }
	     for(set i=0;i<=bottom;i+=scale)
	      {
		          algo.goTo(0,i);
		          algo.lineTo(interval,i);
		          algo.goTo(0,-i);
		          algo.lineTo(interval,-i)
		      }
	   
};
set curve =function(formula)
{      algo.setColor(4);
	  set expt="p="..formula..";";
	set txpt="q="..formula..";";
	
	for (set x=-100;x<=100;x+= inc)
	{   
		 for (set y=-100;y<=0;y+=inc )
		{
			 util.eval(expt);
			 if(p==0)
			{
				
				if(m==0)
				{
					algo.goTo(x*scale1,-(y*scale1));
					m++;
					                           }
				       
				algo.lineTo(scale1*x,-(y*scale1));
			}
			        
		}
	}
	for (set x=100;x>=-100;x-=inc)
	{   
		  for(set y=0;y<=100;y+=inc)
		{
			util.eval(txpt);
			
			if(q==0)
			{
				if(n==0)
				{
					algo.goTo(scale1*x,-(y* scale1));
					n++;
				}
				algo.lineTo(x*scale1,-(y*scale1));
			}
		}
		
	}
};
set formula=getformula();
init();
algo.goTo(0,0);
curve(formula);
algo.hide();
