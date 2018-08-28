package cg.code.aleyam.nzela_nzela.event;

import android.view.View;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class Ftb_event implements View.OnClickListener {
    private FloatingTextButton ftb_source = null;

    private ActionListener listner = null;




    public  void setActionListenr(FloatingTextButton ftb_source , Object implementedClass ) {

        if (implementedClass instanceof ActionListener) {
            Ftb_event event = new Ftb_event();
            event.listner = (ActionListener) implementedClass;
            event.ftb_source = ftb_source;
            event.ftb_source.setOnClickListener(event);
        } else {
            throw new ClassCastException("implemetew l'interface "+ActionListener.class.toString());
        }


    }

    public interface ActionListener {
        public void actionPerformed(FloatingTextButton source);
    }

    @Override
    public void onClick(View v) {
        listner.actionPerformed(ftb_source);
    }
}