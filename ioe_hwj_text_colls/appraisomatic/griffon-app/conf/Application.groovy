application {
    title = 'Appraisomatic'
    startupGroups = ['appraisomatic']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "appraisomatic"
    'appraisomatic' {
        model      = 'uk.bl.spruce.AppraisomaticModel'
        view       = 'uk.bl.spruce.AppraisomaticView'
        controller = 'uk.bl.spruce.AppraisomaticController'
    }

}
