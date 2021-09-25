package com.moganstar.randomtalk.grammar.adapter;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.moganstar.randomtalk.model.Word;

class RecyclerAdapterWordList$4$4 implements OnCancelListener {
   // $FF: synthetic field
   final com.moganstar.randomtalk.grammar.adapter.RecyclerAdapterWordList$4 this$1;

   RecyclerAdapterWordList$4$4(com.moganstar.randomtalk.grammar.adapter.RecyclerAdapterWordList$4 var1) {
      this.this$1 = var1;
   }

   public void onCancel(DialogInterface var1) {
      ((Word)this.this$1.this$0.wordList.get(this.this$1.val$position)).setFlag("white");
      this.this$1.this$0.wordListToBeDisplayed.RemoveFromList((Word)this.this$1.this$0.wordList.get(this.this$1.val$position));
      this.this$1.this$0.myDictionaryInfo.setLevelColor(com.moganstar.randomtalk.grammar.adapter.RecyclerAdapterWordList.access$100(this.this$1.this$0).getLevel_number(), "white");

      for(int var2 = 0; var2 < this.this$1.this$0.wordList.size(); ++var2) {
         if (((Word)this.this$1.this$0.wordList.get(var2)).getFlag().equals("green") || ((Word)this.this$1.this$0.wordList.get(var2)).getFlag().equals("red")) {
            this.this$1.this$0.myDictionaryInfo.setLevelColor(com.moganstar.randomtalk.grammar.adapter.RecyclerAdapterWordList.access$100(this.this$1.this$0).getLevel_number(), "pink");
            break;
         }
      }

      com.moganstar.randomtalk.grammar.adapter.RecyclerAdapterWordList.access$200(this.this$1.this$0);
      this.this$1.this$0.notifyDataSetChanged();
   }
}
