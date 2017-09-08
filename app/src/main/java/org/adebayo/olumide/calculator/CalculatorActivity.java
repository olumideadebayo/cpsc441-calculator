package org.adebayo.olumide.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.view.LayoutInflater;
import android.content.Context;

import java.util.ArrayList;

public class CalculatorActivity extends Activity {

    ArrayList<String> inputs = new ArrayList<String>();
    EditText output ;
    boolean wasOperand = false;
    boolean wasOperation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_layout);
        output = findViewById(R.id.output);
        wasOperation=false;
        wasOperand=false;

        //set all button text size
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calculator_layout, null);

        GridLayout item =  view.findViewById(R.id.gridlayout);
        Log.d("D",item.getChildCount()+"");

        for(int i=0; i<( item.getChildCount()-1);i++){
           if( item.getChildAt(i).hasOnClickListeners()) {
               Button _btn = (Button) item.getChildAt(i);
               Button btn = findViewById(_btn.getId());
               Log.d("D","=="+btn.getText());
               btn.setTextSize(25.0f);

           }
        }
    }
    /* print something in the text area */
    private void show(String s,boolean append){
        if( append){
            output.append(s);
        }else {
            output.setText(s);
        }
    }

    /*clear the screen*/
    public void clearScreenClick(View v){
        output.setText(null);
        reset();
    }
    public void operationClick(View v){
        if( !wasOperation && ! wasOperand){
            clearScreenClick(v);
        }
        String value = ((Button) v).getText().toString();
        if( wasOperation){
            /*
            if we can still do an operation and the current operation sign is unary -
             */
            if( canDoOperation() && value.equalsIgnoreCase("-")){
                inputs.add(value);

            }else{//error in input
                clearScreenClick(v);
                show("Error in input sequence",false);
                reset();
                return;
            }
        }else{
            inputs.add(value);
        }
        wasOperand=false;
        wasOperation=true;
        updateOutput(value);
    }

    public void operandClick(View v){
        if( !wasOperation && ! wasOperand){
            clearScreenClick(v);
        }

        String value = ((Button) v).getText().toString();
        Log.d("D",value);


        if( wasOperand){
            String operand = inputs.remove(inputs.size()-1);
            operand = operand+value;
            inputs.add(operand);
        }else{
            inputs.add(value);
        }
        wasOperand=true;
        wasOperation=false;
        updateOutput(value);

    }
    public void resultRequestClick(View v){
        String result = processInput(inputs);
        result = " = "+result;
        show(result,true);
        reset();

    }

    /*
    resets variables but does not clear screen

     */
    private void reset(){
        wasOperand=false;
        wasOperation=false;
        inputs.clear();
    }
    /*
    will return false iff the last 2 entries are operation signs
     */
    private boolean canDoOperation(){
        if( inputs.size()>=2){
            String a=inputs.get(inputs.size()-1);
            String b=inputs.get(inputs.size()-2);
            if( isOperand(a) && isOperand(b)){
                return false;
            }
        }
        return true;
    }
    /*
    returns true for -,+,/,*
     */
    private boolean isOperand(String a){
        switch(a){
            case "+":
            case "/":
            case "*":
            case "-":
                return true;
            default:
                return false;
        }
    }
    /* update the text area */
    private void updateOutput(String v){
              output.append(v);
    }

    /*
 performs the calculation on a pair 'long' of operands and an operation
  */
    private long calculate(long a,long b,String act){
        long result = a;
        switch (act){
            case "+":
                result += b;
                break;
            case "-":
                result -= b;
                break;
            case "*":
                result *= b;
                break;
            case "/":
                result /= b;
                break;
        }
        return result;
    }

    /*
 performs the calculation on a pair of 'double' operands and an operation
  */
    private double calculate(double a,double b,String act){
        double result = a;
        switch (act){
            case "+":
                result += b;
                break;
            case "-":
                result -= b;
                break;
            case "*":
                result *= b;
                break;
            case "/":
                result /= b;
                break;
        }
        return result;
    }

    /*
    performs the calculation on a pair of operands and an operation
     */
    private String calculate(String a,String b,String act){

        Log.d("D",a);
        Log.d("D",b);
        Log.d("D",act);

        if( a.contains(".") || b.contains("."))
            return  ""+calculate( Double.valueOf(a),Double.valueOf(b),act);

        return ""+calculate( Long.valueOf(a), Long.valueOf(b),act);
    }
    private String processInput(ArrayList<String> input) {

        String runningResult = null;
        String operation = null;
        String leftOperand = null;
        String rightOperand = null;
        boolean isUnaryMinus = false;
        for (int i = 0; i < input.size(); i++) {
            Log.d("D",i+"");

            String token = input.get(i);

            Log.d("D",token);

            switch (token) {
                case "-":
                    //if this is a second operand or the 1st operand in the input
                    if( operation!= null || i==0){
                        isUnaryMinus=true;
                    }else{
                        operation = token;
                    }
                    break;
                case "+":
                case "/":
                case "*":
                    operation = token;
                    break;
                default:
                    if (operation != null) {
                        /*
                        handle division by 0
                         */
                        if( operation.equalsIgnoreCase("/")){
                            if( token.equalsIgnoreCase("0")){
                                reset();
                                show("ERROR DIVISION BY 0",true);
                                return "";
                            }
                        }
                        rightOperand = token;
                        if( isUnaryMinus){
                            rightOperand = "-"+rightOperand;
                            isUnaryMinus=false;
                        }

                        if (runningResult != null) {

                            Log.d("D","aa");

                            runningResult = calculate(runningResult, rightOperand, operation);
                            break;
                        } else {//first operation most likely
                            if (leftOperand != null) {

                                Log.d("D","bbb");

                                runningResult = calculate(leftOperand, rightOperand, operation);
                                leftOperand = null;
                            } else {
                                //error case
                                //TODO
                                reset();
                            }
                        }
                    } else {
                        leftOperand = token;
                        if( isUnaryMinus) {
                            leftOperand = "-"+leftOperand;
                            isUnaryMinus=false;
                        }
                    }
                    operation = null;
                    rightOperand = null;
                    break;
            }
        }
        input.clear();
        return runningResult;
    }



}
