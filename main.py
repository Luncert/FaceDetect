from flexx import flx, app, ui

class Example(flx.Widget):

    def init(self):
        flx.Button(text='hello')
        flx.Button(text='world')
        ui.ImageWidget(source='http://github.com/fluidicon.png')

main = app.launch(Example)
app.run()