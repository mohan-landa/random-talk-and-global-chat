package com.moganstar.randomtalk.grammar.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.moganstar.randomtalk.grammar.VocabularyActivity;
import com.moganstar.randomtalk.model.GridName;

class RecyclerAdapterVocabulary$1 implements OnClickListener {
   // $FF: synthetic field
   final RecyclerAdapterVocabulary this$0;
   // $FF: synthetic field
   final int val$position;

   RecyclerAdapterVocabulary$1(RecyclerAdapterVocabulary var1, int var2) {
      this.this$0 = var1;
      this.val$position = var2;
   }

   public void onClick(View var1) {
      ((VocabularyActivity)this.this$0.myContext).clickGridButton(((GridName)this.this$0.itemList.get(this.val$position)).getTitle());
   }
}
