var gulp = require('gulp');
var del = require('del');
var uglify = require('gulp-uglify');
var usemin = require('gulp-usemin');
var minifyHtml = require('gulp-minify-html');
var rev = require('gulp-rev');

gulp.task('clean', function (cb) {
    del([
        // everything inside the dist folder
        'src/main/webapp/dist/**/*'
    ], cb);
});

gulp.task('usemin', function () {
    return gulp.src('src/main/webapp/*.html')
        .pipe(usemin({
            html: [minifyHtml({empty: true, conditionals:true})],
            js: [uglify(), 'concat', rev()]
        }))
        .pipe(gulp.dest('src/main/webapp/dist'));
});

gulp.task('build', ['clean'], function () {
    gulp.run('usemin');
});