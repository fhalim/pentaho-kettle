package be.ibridge.kettle.trans.step;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.cluster.SlaveServer;
import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.widget.TextVar;


/**
 * 
 * Dialog that allows you to edit the settings of the security service connection
 * 
 * @see SlaveServer
 * @author Matt
 * @since 31-10-2006
 *
 */

public class StepErrorMetaDialog extends Dialog 
{
	private StepErrorMeta stepErrorMeta;
	private List          targetSteps;
    
    private Composite composite;
    private Shell     shell;

    // Service
    private Text     wSourceStep;
    private CCombo   wTargetStep;
    private Button   wEnabled;
	private TextVar  wNrErrors,  wErrDesc, wErrFields,  wErrCodes;

	private Button    wOK, wCancel;
	
    private ModifyListener lsMod;

	private Props     props;

    private int middle;
    private int margin;

    private StepErrorMeta originalStepErrorMeta;
    private boolean ok;
    
	public StepErrorMetaDialog(Shell par, StepErrorMeta stepErrorMeta, List targetSteps)
	{
		super(par, SWT.NONE);
		this.stepErrorMeta=(StepErrorMeta)stepErrorMeta.clone();
        this.originalStepErrorMeta=stepErrorMeta;
        this.targetSteps = targetSteps;
		props=Props.getInstance();
        ok=false;
	}
	
	public boolean open() 
	{
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
 		props.setLook(shell);
		
		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				stepErrorMeta.setChanged();
			}
		};

		middle = props.getMiddlePct();
		margin = Const.MARGIN;

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;
		
		shell.setText("Step error handling settings");
		shell.setLayout (formLayout);
 		
		// First, add the buttons...
		
		// Buttons
		wOK     = new Button(shell, SWT.PUSH); 
		wOK.setText(" &OK ");

		wCancel = new Button(shell, SWT.PUSH); 
		wCancel.setText(" &Cancel ");

		Button[] buttons = new Button[] { wOK, wCancel };
		BaseStepDialog.positionBottomButtons(shell, buttons, margin, null);
		
		// The rest stays above the buttons...
		
        composite = new Composite(shell, SWT.NONE);
        props.setLook(composite);
        composite.setLayout(new FormLayout());

        // What's the source step
        Label wlSourceStep = new Label(composite, SWT.RIGHT); 
        props.setLook(wlSourceStep);
        wlSourceStep.setText("Error handling stepname  ");
        FormData fdlSourceStep = new FormData();
        fdlSourceStep.top   = new FormAttachment(0, 0);
        fdlSourceStep.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlSourceStep.right = new FormAttachment(middle, -margin);
        wlSourceStep.setLayoutData(fdlSourceStep);

        wSourceStep = new Text(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wSourceStep);
        wSourceStep.addModifyListener(lsMod);
        FormData fdSourceStep = new FormData();
        fdSourceStep.top  = new FormAttachment(0, 0);
        fdSourceStep.left = new FormAttachment(middle, 0); // To the right of the label
        fdSourceStep.right= new FormAttachment(95, 0);
        wSourceStep.setLayoutData(fdSourceStep);
        wSourceStep.setEnabled(false);
        
        // What's the target step
        Label wlTargetStep = new Label(composite, SWT.RIGHT); 
        props.setLook(wlTargetStep);
        wlTargetStep.setText("Target step  ");
        FormData fdlTargetStep = new FormData();
        fdlTargetStep.top   = new FormAttachment(wSourceStep, margin);
        fdlTargetStep.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlTargetStep.right = new FormAttachment(middle, -margin);
        wlTargetStep.setLayoutData(fdlTargetStep);

        wTargetStep = new CCombo(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wTargetStep);
        wTargetStep.addModifyListener(lsMod);
        FormData fdTargetStep = new FormData();
        fdTargetStep.top  = new FormAttachment(wSourceStep, margin);
        fdTargetStep.left = new FormAttachment(middle, 0); // To the right of the label
        fdTargetStep.right= new FormAttachment(95, 0);
        wTargetStep.setLayoutData(fdTargetStep);
        for (int i=0;i<targetSteps.size();i++)
        {
            wTargetStep.add( ((StepMeta)targetSteps.get(i)).getName() );
        }

        // is the error handling enabled?
        Label wlEnabled = new Label(composite, SWT.RIGHT); 
        props.setLook(wlEnabled);
        wlEnabled.setText("Enable the error handling?  ");
        FormData fdlEnabled = new FormData();
        fdlEnabled.top   = new FormAttachment(wTargetStep, margin);
        fdlEnabled.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlEnabled.right = new FormAttachment(middle, -margin);
        wlEnabled.setLayoutData(fdlEnabled);

        wEnabled = new Button(composite, SWT.CHECK );
        props.setLook(wEnabled);
        FormData fdEnabled = new FormData();
        fdEnabled.top  = new FormAttachment(wTargetStep, margin);
        fdEnabled.left = new FormAttachment(middle, 0); // To the right of the label
        wEnabled.setLayoutData(fdEnabled);

        // What's the field for the nr of errors
        Label wlNrErrors = new Label(composite, SWT.RIGHT); 
        props.setLook(wlNrErrors);
        wlNrErrors.setText("Nr of errors fieldname  ");
        FormData fdlNrErrors = new FormData();
        fdlNrErrors.top   = new FormAttachment(wEnabled, margin*2);
        fdlNrErrors.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlNrErrors.right = new FormAttachment(middle, -margin);
        wlNrErrors.setLayoutData(fdlNrErrors);

        wNrErrors = new TextVar(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wNrErrors);
        wNrErrors.addModifyListener(lsMod);
        FormData fdNrErrors = new FormData();
        fdNrErrors.top  = new FormAttachment(wEnabled, margin*2);
        fdNrErrors.left = new FormAttachment(middle, 0); // To the right of the label
        fdNrErrors.right= new FormAttachment(95, 0);
        wNrErrors.setLayoutData(fdNrErrors);

        // What's the field for the error descriptions
        Label wlErrDesc = new Label(composite, SWT.RIGHT); 
        props.setLook(wlErrDesc);
        wlErrDesc.setText("Error descriptions fieldname  ");
        FormData fdlErrDesc = new FormData();
        fdlErrDesc.top   = new FormAttachment(wNrErrors, margin);
        fdlErrDesc.left  = new FormAttachment(0, 0);  // First one in the left top corner
        fdlErrDesc.right = new FormAttachment(middle, -margin);
        wlErrDesc.setLayoutData(fdlErrDesc);

        wErrDesc = new TextVar(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wErrDesc);
        wErrDesc.addModifyListener(lsMod);
        FormData fdErrDesc = new FormData();
        fdErrDesc.top  = new FormAttachment(wNrErrors, margin);
        fdErrDesc.left = new FormAttachment(middle, 0); // To the right of the label
        fdErrDesc.right= new FormAttachment(95, 0);
        wErrDesc.setLayoutData(fdErrDesc);
        
        // What's the field for the error fields
        Label wlErrFields = new Label(composite, SWT.RIGHT ); 
        wlErrFields.setText("Error fields fieldname  "); 
        props.setLook(wlErrFields);
        FormData fdlErrFields = new FormData();
        fdlErrFields.top  = new FormAttachment(wErrDesc, margin);
        fdlErrFields.left = new FormAttachment(0,0); 
        fdlErrFields.right= new FormAttachment(middle, -margin);
        wlErrFields.setLayoutData(fdlErrFields);

        wErrFields = new TextVar(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wErrFields);
        wErrFields.addModifyListener(lsMod);
        FormData fdErrFields = new FormData();
        fdErrFields.top  = new FormAttachment(wErrDesc, margin);
        fdErrFields.left = new FormAttachment(middle, 0); 
        fdErrFields.right= new FormAttachment(95, 0);
        wErrFields.setLayoutData(fdErrFields);
        
        // What's the fieldname for the error codes field
        Label wlErrCodes = new Label(composite, SWT.RIGHT ); 
        wlErrCodes.setText("Error codes fieldname  "); 
        props.setLook(wlErrCodes);
        FormData fdlErrCodes = new FormData();
        fdlErrCodes.top  = new FormAttachment(wErrFields, margin);
        fdlErrCodes.left = new FormAttachment(0,0);
        fdlErrCodes.right= new FormAttachment(middle, -margin);
        wlErrCodes.setLayoutData(fdlErrCodes);

        wErrCodes = new TextVar(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
        props.setLook(wErrCodes);
        wErrCodes.addModifyListener(lsMod);
        FormData fdErrCodes = new FormData();
        fdErrCodes.top  = new FormAttachment(wErrFields, margin);
        fdErrCodes.left = new FormAttachment(middle, 0); 
        fdErrCodes.right= new FormAttachment(95, 0);
        wErrCodes.setLayoutData(fdErrCodes);

		FormData fdComposite = new FormData();
		fdComposite.left  = new FormAttachment(0, 0);
		fdComposite.top   = new FormAttachment(0, 0);
		fdComposite.right = new FormAttachment(100, 0);
		fdComposite.bottom= new FormAttachment(wOK, -margin);
		composite.setLayoutData(fdComposite);
		
		// Add listeners
		wOK.addListener(SWT.Selection, new Listener () { public void handleEvent (Event e) { ok(); } } );
        wCancel.addListener(SWT.Selection, new Listener () { public void handleEvent (Event e) { cancel(); } } );
		
        SelectionAdapter selAdapter=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		wErrFields.addSelectionListener(selAdapter);
		wErrCodes.addSelectionListener(selAdapter);
		wNrErrors.addSelectionListener(selAdapter);
        wErrDesc.addSelectionListener(selAdapter);


		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );
	    
		getData();

		BaseStepDialog.setSize(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return ok;
	}
	

    public void dispose()
	{
		props.setScreen(new WindowProperty(shell));
		shell.dispose();
	}
    
    public void getData()
	{
        wSourceStep.setText( stepErrorMeta.getSourceStep() != null ? stepErrorMeta.getSourceStep().getName() : "" );
        wTargetStep.setText( stepErrorMeta.getTargetStep() != null ? stepErrorMeta.getTargetStep().getName() : "" );
        wEnabled.setSelection( stepErrorMeta.isEnabled() );
        wNrErrors.setText( Const.NVL(stepErrorMeta.getNrErrorsValuename(), "") );
        wErrDesc.setText( Const.NVL(stepErrorMeta.getErrorDescriptionsValuename(), "") );
        wErrFields.setText( Const.NVL(stepErrorMeta.getErrorFieldsValuename(), "") );
		wErrCodes.setText( Const.NVL(stepErrorMeta.getErrorCodesValuename(), "") );

		wSourceStep.setFocus();
	}
    
	private void cancel()
	{
		originalStepErrorMeta = null;
		dispose();
	}
	
	public void ok()
	{
        getInfo();
        originalStepErrorMeta.setTargetStep( stepErrorMeta.getTargetStep() );
        originalStepErrorMeta.setEnabled( stepErrorMeta.isEnabled() );
        originalStepErrorMeta.setNrErrorsValuename( stepErrorMeta.getNrErrorsValuename() );
        originalStepErrorMeta.setErrorDescriptionsValuename( stepErrorMeta.getErrorDescriptionsValuename() );
        originalStepErrorMeta.setErrorFieldsValuename( stepErrorMeta.getErrorFieldsValuename() );
        originalStepErrorMeta.setErrorCodesValuename( stepErrorMeta.getErrorCodesValuename() );

        originalStepErrorMeta.setChanged();

        ok=true;
        
        dispose();
	}
    
    // Get dialog info in securityService
	private void getInfo()
    {
        stepErrorMeta.setTargetStep( StepMeta.findStep(targetSteps, wTargetStep.getText()) );
        stepErrorMeta.setEnabled( wEnabled.getSelection() );
        stepErrorMeta.setNrErrorsValuename( wNrErrors.getText() );
        stepErrorMeta.setErrorDescriptionsValuename( wErrDesc.getText() );
        stepErrorMeta.setErrorFieldsValuename( wErrFields.getText() );
        stepErrorMeta.setErrorCodesValuename( wErrCodes.getText() );
    }
}