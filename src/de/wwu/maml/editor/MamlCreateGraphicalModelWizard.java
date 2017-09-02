package de.wwu.maml.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.xmi.XMLResource;

import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.dialect.command.CreateRepresentationCommand;
import org.eclipse.sirius.business.api.helper.SiriusResourceHelper;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.tools.api.command.semantic.AddSemanticResourceCommand;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelection;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelectionCallbackWithConfimation;
import org.eclipse.sirius.ui.business.internal.commands.ChangeViewpointSelectionCommand;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eclipse.ui.actions.WorkspaceModifyOperation;

import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import org.eclipse.ui.part.ISetSelectionTarget;

import de.wwu.maml.dsl.maml.MamlFactory;
import de.wwu.maml.dsl.maml.MamlPackage;
import de.wwu.maml.dsl.maml.presentation.MAMLEditorPlugin;

import org.eclipse.core.runtime.Path;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;


/**
 * This is a simple wizard for creating a new model file.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
@SuppressWarnings("restriction")
public class MamlCreateGraphicalModelWizard extends Wizard implements INewWizard
{
	/**
	 * The supported extensions for created files.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<String> FILE_EXTENSIONS =
		Collections.unmodifiableList(Arrays.asList(MAMLEditorPlugin.INSTANCE.getString("_UI_MamlEditorFilenameExtensions").split("\\s*,\\s*")));

	/**
	 * A formatted list of supported file extensions, suitable for display.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String FORMATTED_FILE_EXTENSIONS =
		MAMLEditorPlugin.INSTANCE.getString("_UI_MamlEditorFilenameExtensions").replaceAll("\\s*,\\s*", ", ");

	/**
	 * This caches an instance of the model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MamlPackage mamlPackage = MamlPackage.eINSTANCE;

	/**
	 * This caches an instance of the model factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MamlFactory mamlFactory = mamlPackage.getMamlFactory();

	/**
	 * This is the file creation page.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MamlModelWizardNewFileCreationPage newFileCreationPage;

	/**
	 * Remember the selection during initialization for populating the default container.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IStructuredSelection selection;

	/**
	 * Remember the workbench during initialization.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IWorkbench workbench;

	/**
	 * Caches the names of the types that can be created as the root object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected List<String> initialObjectNames;

	/**
	 * This just records the information.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle(MAMLEditorPlugin.INSTANCE.getString("_UI_Wizard_label"));
		setDefaultPageImageDescriptor(ExtendedImageRegistry.INSTANCE.getImageDescriptor(MAMLEditorPlugin.INSTANCE.getImage("full/wizban/NewMaml")));
	}

	/**
	 * Create a new model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EObject createInitialModel()
	{
		EClass eClass = (EClass)mamlPackage.getEClassifier("UseCase");
		EObject rootObject = mamlFactory.create(eClass);
		return rootObject;
	}
	
	protected ArrayList<String> getEncodings()
	{
		ArrayList<String> encodings = new ArrayList<String>();
		for (StringTokenizer stringTokenizer = new StringTokenizer(MAMLEditorPlugin.INSTANCE.getString("_UI_XMLEncodingChoices")); stringTokenizer.hasMoreTokens(); ) {
			encodings.add(stringTokenizer.nextToken());
		}
		return encodings;
	}

	/**
	 * Do the work after everything is specified.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean performFinish()
	{
		try {
			// Remember the file and resource.
			final IFile modelFile = getModelFile();
			
			// Get the URI of the model file.
			URI fileURI = URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true);
			
			// Create a resource set
			ResourceSet resourceSet = new ResourceSetImpl();

			// Create a resource for this file.
			Resource resource = resourceSet.createResource(fileURI);
			
			// Do the work within an operation.
			WorkspaceModifyOperation operation =
				new WorkspaceModifyOperation() {
					@Override
					protected void execute(IProgressMonitor progressMonitor) {
						try {
							// Add the initial model object to the contents.
							//
							EObject rootObject = createInitialModel();
							if (rootObject != null) {
								resource.getContents().add(rootObject);
							}

							// Save the contents of the resource to the file system.
							//
							Map<Object, Object> options = new HashMap<Object, Object>();
							options.put(XMLResource.OPTION_ENCODING, getEncodings().get(0));
							resource.save(options);
						}
						catch (Exception exception) {
							MAMLEditorPlugin.INSTANCE.log(exception);
						}
						finally {
							progressMonitor.done();
						}
					}
				};

			getContainer().run(false, false, operation);

			// Select the new file resource in the current view.
			//
			IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			final IWorkbenchPart activePart = page.getActivePart();
			if (activePart instanceof ISetSelectionTarget) {
				final ISelection targetSelection = new StructuredSelection(modelFile);
				getShell().getDisplay().asyncExec
					(new Runnable() {
						 public void run() {
							 ((ISetSelectionTarget)activePart).selectReveal(targetSelection);
						 }
					 });
			}

			// Create viewpoint
			IFile airdFile = newFileCreationPage.getRepresentationsFile();
			if(!airdFile.exists())
			    throw new Exception("could not found file:" + airdFile.getLocationURI());
			URI airdFileURI = URI.createPlatformResourceURI(airdFile.getFullPath().toOSString(), true);
			
			IProgressMonitor monitor = new NullProgressMonitor();
			Session session = SessionManager.INSTANCE.getSession(airdFileURI, monitor);
			session.save(monitor);
			
			//adding the resource to Sirius session
			if(resource == null) return false;
			AddSemanticResourceCommand addCommandToSession = new AddSemanticResourceCommand(session, resource.getURI(), monitor);
			session.getTransactionalEditingDomain().getCommandStack().execute(addCommandToSession);
			                		
			//find and add viewpoint
			Set<Viewpoint> availableViewpoints = ViewpointSelection.getViewpoints(getModelFile().getFileExtension());
			if(availableViewpoints.isEmpty())
			      throw new Exception("Could not found viewpoint for file extension " + getModelFile().getFileExtension());
			                		
			ViewpointSelection.Callback callback = new ViewpointSelectionCallbackWithConfimation();
			
			Set<Viewpoint> viewpoints = new HashSet<Viewpoint>();
			for(Viewpoint p : availableViewpoints)
				viewpoints.add(SiriusResourceHelper.getCorrespondingViewpoint(session, p));

			RecordingCommand command = new ChangeViewpointSelectionCommand(
			                				session,
			                				callback,
			                				viewpoints, new HashSet<Viewpoint>(), monitor);
			                		
			session.getTransactionalEditingDomain().getCommandStack().execute(command);
			
			// Semantic root object (!= root created above)
			// Find last object = newest resource
			EObject rootObject = null;
			Iterator<Resource> iter = session.getSemanticResources().iterator();
			while(iter.hasNext()){
				rootObject = iter.next().getContents().get(0);
			}
			
			// Create representation
			Collection<RepresentationDescription> descriptions = DialectManager.INSTANCE.getAvailableRepresentationDescriptions(session.getSelectedViewpoints(false),  rootObject );
			if(descriptions.isEmpty())
				throw new Exception("Could not found representation description for object: " + rootObject);
			RepresentationDescription description = descriptions.iterator().next();

			DialectManager viewpointDialectManager = DialectManager.INSTANCE;
			Command createViewCommand = new CreateRepresentationCommand(session,
					  description, rootObject, "MAML editor", monitor);
			 
			session.getTransactionalEditingDomain().getCommandStack().execute(createViewCommand);

			SessionManager.INSTANCE.notifyRepresentationCreated(session);

			//open editor 
			Collection<DRepresentation> representations = viewpointDialectManager.getRepresentations(description, session);
			Iterator<DRepresentation> iter2 = representations.iterator();
			DRepresentation myDiagramRepresentation = iter2.next();
			while(iter2.hasNext()){
				myDiagramRepresentation = iter2.next();
			}

			DialectUIManager dialectUIManager = DialectUIManager.INSTANCE;
				dialectUIManager.openEditor(session,
					myDiagramRepresentation, monitor);
			   
			//save session and refresh workspace
			session.save(monitor);
			//project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			return true;
		}
		catch (Exception exception) {
			MAMLEditorPlugin.INSTANCE.log(exception);
			return false;
		}
	}

	/**
	 * This is the one page of the wizard.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public class MamlModelWizardNewFileCreationPage extends WizardNewFileCreationPage
	{
		/**
		 * Pass in the selection.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public MamlModelWizardNewFileCreationPage(String pageId, IStructuredSelection selection)
		{
			super(pageId, selection);
		}

		/**
		 * The framework calls this to see if the file is correct.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		@Override
		protected boolean validatePage()
		{
			if (super.validatePage()) {
				String extension = new Path(getFileName()).getFileExtension();
				if (extension == null || !FILE_EXTENSIONS.contains(extension)) {
					String key = FILE_EXTENSIONS.size() > 1 ? "_WARN_FilenameExtensions" : "_WARN_FilenameExtension";
					setErrorMessage(MAMLEditorPlugin.INSTANCE.getString(key, new Object [] { FORMATTED_FILE_EXTENSIONS }));
					return false;
				}
				return true;
			}
			return false;
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		public IFile getModelFile()
		{
			return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName()));
		}
		
		public IFile getRepresentationsFile() {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append("representations.aird"));
		}
	}

	/**
	 * The framework calls this to create the contents of the wizard.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
		@Override
	public void addPages()
	{
		// Create a page, set the title, and the initial model file name.
		//
		newFileCreationPage = new MamlModelWizardNewFileCreationPage("Whatever", selection);
		newFileCreationPage.setTitle(MAMLEditorPlugin.INSTANCE.getString("_UI_MamlModelWizard_label"));
		newFileCreationPage.setDescription(MAMLEditorPlugin.INSTANCE.getString("_UI_MamlModelWizard_description"));
		newFileCreationPage.setFileName(MAMLEditorPlugin.INSTANCE.getString("_UI_MamlEditorFilenameDefaultBase") + "." + FILE_EXTENSIONS.get(0));
		addPage(newFileCreationPage);

		// Try and get the resource selection to determine a current directory for the file dialog.
		//
		if (selection != null && !selection.isEmpty()) {
			// Get the resource...
			//
			Object selectedElement = selection.iterator().next();
			if (selectedElement instanceof IResource) {
				// Get the resource parent, if its a file.
				//
				IResource selectedResource = (IResource)selectedElement;
				if (selectedResource.getType() == IResource.FILE) {
					selectedResource = selectedResource.getParent();
				}

				// This gives us a directory...
				//
				if (selectedResource instanceof IFolder || selectedResource instanceof IProject) {
					// Set this for the container.
					//
					newFileCreationPage.setContainerFullPath(selectedResource.getFullPath());

					// Make up a unique new name here.
					//
					String defaultModelBaseFilename = MAMLEditorPlugin.INSTANCE.getString("_UI_MamlEditorFilenameDefaultBase");
					String defaultModelFilenameExtension = FILE_EXTENSIONS.get(0);
					String modelFilename = defaultModelBaseFilename + "." + defaultModelFilenameExtension;
					for (int i = 1; ((IContainer)selectedResource).findMember(modelFilename) != null; ++i) {
						modelFilename = defaultModelBaseFilename + i + "." + defaultModelFilenameExtension;
					}
					newFileCreationPage.setFileName(modelFilename);
				}
			}
		}
	}

	/**
	 * Get the file from the page.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IFile getModelFile()
	{
		return newFileCreationPage.getModelFile();
	}

}
