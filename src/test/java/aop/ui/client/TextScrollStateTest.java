package aop.ui.client;

/** Tests visible motion semantics independently of rendering and frame timing. */
public final class TextScrollStateTest {
    private static int checks;
    private static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
    private static double sample(TextScrollState state,int start,int end,int interval,double limit,boolean fast){double value=0;for(int t=start;t<=end;t+=interval)value=state.advance(t,limit,fast);return value;}
    public static void main(String[] args){
        var slow=new TextScrollState();check(slow.advance(0,100,false)==0,"starts at beginning");check(slow.advance(200,100,false)==0,"initial pause");
        double normal=sample(new TextScrollState(),0,2000,10,500,false);
        double hover=sample(new TextScrollState(),0,2000,10,500,true);check(hover>normal*2,"hover is faster");
        double fps30=sample(new TextScrollState(),0,9990,30,80,false),fps100=sample(new TextScrollState(),0,9990,10,80,false);check(Math.abs(fps30-fps100)<.001,"frame independent");
        var state=new TextScrollState();double previous=0;boolean end=false,returned=false,endPause=false;int held=0;
        for(int t=0;t<=22000;t+=20){double value=state.advance(t,24,false);check(value>=0&&value<=24,"viewport bound");if(value==24){end=true;held++;if(held>20)endPause=true;}if(end&&value==0)returned=true;previous=value;}
        check(end,"reaches full suffix");check(endPause,"readable pause at end");check(returned,"returns to prefix");
        double before=sample(state,22020,22500,20,120,false);double after=state.advance(22501,120,true);check(Math.abs(after-before)<1,"hover does not jump");
        check(state.advance(22520,0,true)==0,"fitting text resets");check(state.advance(22540,60,false)==0,"resize resets");
        state.reset();check(state.advance(30000,60,false)==0,"replacement starts at prefix");
        System.out.println("AOP_TEXT_SCROLL_PASS checks="+checks);
    }
}
