package com.emf.rubenbp.genmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class Main {

	public static void main(String[] args) {
		
//		String ecorePath=args[0];
//		String genmodelName=args[1];
//		String srcGenPath=args[2];
//		String projectName=args[3];
		
		String ecorePath="/Users/rubenbp/eclipse-maven/example/model/emfmodel.ecore";
		String genmodelName="emfmode3.genmodel";
		String srcGenPath="/example/src-gen2";
		String projectName="example";
//		
	
		
		try {
			createGenModel(ecorePath,genmodelName,srcGenPath, projectName, projectName, "", projectName);
			//generateCode();

			//createGenModel("./model/eMFProject.ecore", "eMFProject.genmodel", "/pruebaGEN/src", "pruebaGEN", "pruebaGEN", "testpackage", "pruebaGEN");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void generateCode() 
	{
		ProcessBuilder pb;
	

		  pb = new ProcessBuilder("/bin/bash", "-c", "./eclipse -noSplash -data /Users/rubenbp/eclipse-maven -model -edit -tests -application org.eclipse.emf.codegen.ecore.Generator -projects /Users/rubenbp/eclipse-maven/example -reconcile /Users/rubenbp/eclipse-maven/example/emfmodel.genmodel ");
		  pb.directory(new File("/Users/rubenbp/testeo/Eclipse.app/Contents/MacOs"));
		  try {
			pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getMetaModelFilePath(final File folder) {
		String metaModelFilePath = "";
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				getMetaModelFilePath(fileEntry);
			} else {
				if (fileEntry.getName().contains(".ecore")) {
					metaModelFilePath = fileEntry.getPath();

				}

			}
		}
		return metaModelFilePath;
	}
	public static EPackage loadECoreFile() {

		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
		Resource res = rs.createResource(URI.createFileURI(getMetaModelFilePath(new File("/Users/rubenbp/eclipse-maven/example/model"))));
		try {
			res.load(null);
		} catch (IOException e) {

			e.printStackTrace();
		}
		EPackage metapackage = (EPackage) res.getContents().get(0);

		return metapackage;

	}
	public static GenModel createGenModel(final String ecoreLocation,final String genModelDirectory, final String modelCodeDirectory, final String modelPlugInID, final String modelName, final String basePackage, final String prefix)
			throws IOException {
		GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
		genModel.setComplianceLevel(GenJDKLevel.JDK70_LITERAL);
		genModel.setModelDirectory(modelCodeDirectory);
		genModel.setModelPluginID(modelPlugInID);
		genModel.getForeignModel().add(new Path(ecoreLocation).lastSegment());
		genModel.setModelName(modelName);
		// genModel.setRootExtendsInterface(Constants.GEN_MODEL_EXTENDS_INTERFACE.getValue());
		genModel.initialize(Collections.singleton(loadECoreFile()));
		
		GenPackage genPackage = genModel.getGenPackages().get(0);
		genPackage.setPrefix(prefix);
		genPackage.setBasePackage(basePackage);

		try {
			URI genModelURI = URI.createFileURI(genModelDirectory);
			final XMIResourceImpl genModelResource = new XMIResourceImpl(
					genModelURI);
			// genModelResource.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING,
			// Constants.GEN_MODEL_XML_ENCODING.getValue());
			genModelResource.getContents().add(genModel);
			genModelResource.save(Collections.EMPTY_MAP);
			
			genModel.setCanGenerate(true);
//			genModel.generate(new BasicMonitor.Printing(System.err));
		} catch (IOException e) {
			String msg = null;
			if (e instanceof FileNotFoundException) {
				msg = "Unable to open output file ";
			} else {
				msg = "Unexpected IO Exception writing ";
			}
			throw new RuntimeException(msg, e);
		}
		return genModel;
	}
	
	
}
