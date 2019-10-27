const gulp = require('gulp')
const electron = require('electron-connect').server.create()
const ts = require('gulp-typescript')
 
var tsProject = ts.createProject('tsconfig.json')

function wrap(f) {
    return () => {
        tsProject.src()
            .pipe(tsProject())
            .js.pipe(gulp.dest('dist'))
        f()
    }
}

gulp.task('watch:electron', function () {
    electron.start('dist/main.js')
    gulp.watch(['index.html'], electron.reload)
    gulp.watch(['src/main.ts'], wrap(electron.restart))
    gulp.watch(['src/serv/**/*', 'src/view/**/*'], wrap(electron.reload))
});