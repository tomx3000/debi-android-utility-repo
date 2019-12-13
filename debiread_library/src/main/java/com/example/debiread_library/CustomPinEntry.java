package com.example.debiread_library;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

public class CustomPinEntry {

    private TextWatcher textWatcher;
    private Context context;
    private LinearLayout layout;
    private ArrayList<EditText> input_fields = new ArrayList<>();
    private ArrayList<String> pin = new ArrayList<>();
    private CustomPinEntryListener customPinEntryListener;
    private String input_type = "number";

    public CustomPinEntry(Context context, LinearLayout layoutl){
    this.context = context;
    this.layout  = layoutl;
        initializeInputFields(4);
        monitorPinInput();
        this.customPinEntryListener = null;
    }

    public CustomPinEntry(Context context, LinearLayout layoutl, String input_type){
        this.input_type = input_type;
        this.context = context;
        this.layout  = layoutl;
        initializeInputFields(4);
        monitorPinInput();
        this.customPinEntryListener = null;
    }

    public CustomPinEntry(Context context, LinearLayout layoutl, int number_of_fields){
        this.context = context;
        this.layout  = layoutl;
        initializeInputFields(number_of_fields);
        monitorPinInput();
        this.customPinEntryListener = null;
    }

    public interface CustomPinEntryListener{
        public void onPinReady(String pin);
        public void pinLiveFeed(String pin);
    }


    public void setCustomPinEntryListener(CustomPinEntryListener customPinEntryListener) {
        this.customPinEntryListener = customPinEntryListener;
    }

    public void initializeInputFields(int number_of_fields){
        int maxlenght = 1;
        InputFilter[] filterArrays = new InputFilter[1];
        filterArrays[0] = new InputFilter.LengthFilter(maxlenght);

        if(number_of_fields <=0){
            number_of_fields = 4;
        }
        for(int i=0; i<number_of_fields; i++){
            EditText editText = new EditText(context);
            if(input_type.equalsIgnoreCase("password")){
                editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }else{
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            editText.setId(i);
            editText.setFilters(filterArrays);
//            remmeber this is pixel reference and not dp, thus find a better way
            editText.setHint("0");
            editText.setWidth(100);
            editText.setMaxLines(1);
            input_fields.add(editText);
            layout.addView(editText);
//            Collections.reverse(input_fields);
        }

    }

    public void monitorPinInput(){
        for(final EditText editText: input_fields){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int next_positon ;

                    int current_position = input_fields.indexOf(editText);

                    if(current_position+1==input_fields.size())next_positon = current_position;
                    else next_positon = current_position+1;

                    if(editText.length()==1){
                        pin.add(editText.getText().toString());
                        input_fields.get(next_positon).requestFocus();

                    }

                    if(pin.size()==4){
//                        String temp_pin="";
//                        for(String temp: pin){
//                            temp_pin+=temp;
//                        }
                        customPinEntryListener.onPinReady(extractPin(pin));
                    }

//                    sends live feed on input of each pin
                    customPinEntryListener.pinLiveFeed(extractPin(pin));

                }
            });


            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    int previous_position;
                    int current_position = input_fields.indexOf(editText);
                    if(current_position == 0)
                        previous_position = current_position;
                    else previous_position =current_position-1;

                    if(keyCode == KeyEvent.KEYCODE_DEL){
                        if(pin.size()>0){

//                          tried to deletion by purely index, it worked for some time but eventually failed once i intoduced it to delete card activity... do not  know why though ( though the problem lies in the loop and listener.. since it behaves as some one is caling the delete event twice)

//                            well the soulution proposed was to delete the string object its self instead of referncing the index. so inoder to do this propely one has to start deleting from the en d of the list , but and then reversing the list again for display
                            Collections.reverse(pin);
                            pin.remove(editText.getText().toString());
                            Collections.reverse(pin);
//                            pin.remove(pin.size()-1);
                        }
//                        once deleted go back
                        if(editText.length()==0){
                            input_fields.get(previous_position).requestFocus();
                        }
                    }

                    return false;
                }
            });


            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        int next_positon ;
                        int previous_position;
                        int current_position = input_fields.indexOf(editText);

                        if(current_position+1==input_fields.size())next_positon = current_position;
                        else next_positon = current_position+1;

                        if(current_position == 0)
                            previous_position = current_position;
                        else previous_position =current_position-1;

                        if(editText.length()==0){
//                            if empty
//                            can only go backward
                            if(current_position==0){
//                                if at the last position
//                                stay there
                            }else{
                                if(input_fields.get(previous_position).length()==1){
//                                    if previous entry exists
//                                    stay at current position
                                }else if(input_fields.get(previous_position).length()==0){
//                                    if previous is empty
//                              go back
                                    input_fields.get(previous_position).requestFocus();

                                }

                            }
                        }else if(editText.length()==1){
//                            if not empty
//                            can only go forwad
                            if(current_position+1 == input_fields.size()){
//                                if at the final position
//                                stay there
                            }else{
//                                otherwise go forward
                                if(input_fields.get(next_positon).length()==0){
//                                    if next item is empty
//                                    do nothing
                                }
                                else if(input_fields.get(next_positon).length()==1){
//                                    must go forward if next item is not empty
                                    input_fields.get(next_positon).requestFocus();
                                }

                            }
                        }

                    }
                }
            });

        }

    }

//    once you call this you should hadle the event your self since , you are guranteed to pass
    public void setPin(String input_pin){
        pin.clear();
        for(EditText editText: input_fields){
            editText.setText(input_pin.substring(input_fields.indexOf(editText),input_fields.indexOf(editText)+1));
            pin.add(input_pin.substring(input_fields.indexOf(editText),input_fields.indexOf(editText)+1));
        }


    }

//    on failure call reset
    public void resetInputs(){
        pin.clear();
        for (EditText editText:input_fields){
            editText.setText("");
        }
    }

    public String extractPin(ArrayList<String> input_pin){
        String temp = "";
        for(String temp_pin:input_pin){
            temp+=temp_pin;
        }
        return temp;
    }

//    do not use this for now
    public void setInputType(String type){
        if(type.equalsIgnoreCase("password")){
            for(EditText editText:input_fields  ){
                editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }
        }else if(type.equalsIgnoreCase("number")){
            for(EditText editText:input_fields){
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }
    }

    public int getIndexIttemLastFound(String item,ArrayList<String> alist){
        for(int i=alist.size()-1; i>=0; i--){
            if(alist.get(i).equals(item))
                return  i;
        }
//        it will never reach the below statement,
//        since i make sure it contains the value before i call it
        return 0;
    }

}
