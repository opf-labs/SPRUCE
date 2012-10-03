package uk.bl.spruce

import griffon.util.GriffonNameUtils as GNU

fileChooserWindow = fileChooser(
	fileSelectionMode: JFileChooser.DIRECTORIES_ONLY
)

application(title: 'appraisomatic',
	pack: true,
	locationByPlatform: true,
	iconImage: imageIcon('/griffon-icon-48x48.png').image,
	iconImages: [imageIcon('/griffon-icon-48x48.png').image,
				imageIcon('/griffon-icon-32x32.png').image,
				imageIcon('/griffon-icon-16x16.png').image ]) {
			borderLayout()
			panel(constraints: CENTER,
				border: titledBorder(title: 'Configure Directories')) {
				migLayout()
				label('Collection Path', constraints: 'left')
				textField(columns: 20, constraints: 'growx, wrap',
				text: bind("collectionPath", target: model, mutual: true))
				label('Output Path', constraints: 'left')
				textField(columns: 20, constraints: 'growx, wrap',
				text: bind("outputPath", target: model, mutual: true))
			}
			panel(constraints: EAST,
				border: titledBorder(title: 'Actions')) {
				migLayout()
				button('Set Collection Path', actionPerformed: controller.setCollectionPath, constraints: 'growx, wrap')
				button('Set Output Path', actionPerformed: controller.setOutputPath, constraints: 'growx, wrap')
				button('Process', actionPerformed: controller.process, constraints: 'growx, wrap')
				button('Clear', actionPerformed: controller.clear, constraints: 'growx, wrap')
				button('Quit', actionPerformed: controller.quit, constraints: 'growx, wrap')
//				controller.griffonClass.actionNames.each { name ->
//					button(GNU.getNaturalName(name), actionPerformed: controller."$name", constraints: 'growx, wrap')
//				}
			}
			panel(constraints: SOUTH,
				border: titledBorder(title: 'Status')) {
				migLayout()
				label(text: bind{ model.statusMsg })
			}
}
