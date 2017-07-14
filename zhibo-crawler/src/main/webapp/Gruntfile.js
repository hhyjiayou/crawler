/**
 * Created by yiwei on 15/10/27.
 */
'use strict';

module.exports = function (grunt) {
  require('load-grunt-tasks')(grunt);
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    config: {
      src: './fe_src/app',
      src_bower: './fe_src/bower_components',
      dest: './crawler'
    },
    copy: {
      release_yw: {
        files: [
          // copy all images in app/images/**/
          {
            expand: true,
            cwd: '<%=config.src%>/images',
            src: ['**/*.png', '**/*.jpeg', '**/*.jpg', '**/*.gif', '**/*.icon', '**/*.svg', '**/*.swf'],
            dest: '<%=config.dest%>/static/images'
          },
          //copy all *.html  && *.jsp in app/**/*.html
          {
            expand: true,
            cwd: '<%=config.src%>/views/',
            src: ['**/*.html', '**/*.jsp'],
            dest: '<%=config.dest%>/views'
          },
          {
            expand: true,
            cwd: '<%=config.src%>/',
            src: ['*.html', '*.jsp'],
            dest: '<%=config.dest%>/views'
          },
          //copy all font file
          {
            expand: true,
            cwd: './fe_src/bower_components/bootstrap/fonts',
            src: ['*.ttf', '*.woff', '*.woff2', '*.svg', '*.eot'],
            dest: '<%=config.dest%>/static/fonts'
          },
         {
            expand: true,
            cwd:  '<%=config.src%>/scripts/directives',
            src: ['*.html','*.png'],
            dest: '<%=config.dest%>/static/scripts/directives'
         }
        ]
      }
    },
    // concat css js
    concat: {
      options: {
        separator: ';'
      },
      release_yw: {
        files: {
          '<%=config.dest%>/static/scripts/lib/lib.js': [
            '<%=config.src_bower%>/jquery/dist/jquery.js',
            '<%=config.src_bower%>/angular/angular.js',
            '<%=config.src_bower%>/bootstrap/dist/js/bootstrap.js',
            '<%=config.src_bower%>/angular-animate/angular-animate.js',
            '<%=config.src_bower%>/angular-cookies/angular-cookies.js',
            '<%=config.src_bower%>/angular-resource/angular-resource.js',
            '<%=config.src_bower%>/angular-route/angular-route.js',
            '<%=config.src_bower%>/angular-sanitize/angular-sanitize.js',
            '<%=config.src_bower%>/angular-touch/angular-touch.js'
          ],
          '<%=config.dest%>/static/scripts/services/service.js': [
            '<%=config.src%>/scripts/services/*.js'
          ],
          '<%=config.dest%>/static/scripts/filters/filter.js': [
            '<%=config.src%>/scripts/filters/*.js'
          ],
          '<%=config.dest%>/static/scripts/directives/directive.js': [
            '<%=config.src%>/scripts/directives/*.js'
          ],
          '<%=config.dest%>/static/scripts/controllers/app.js': [
            '<%=config.src%>/scripts/controllers/app.js'
          ],
          '<%=config.dest%>/static/scripts/util/ZeroClipboard.js': [
            '<%=config.src%>/scripts/util/ZeroClipboard.js'
          ],
          '<%=config.dest%>/static/scripts/controllers/resourceMannager.js': [
            '<%=config.src%>/scripts/controllers/resourceMannager.js'
          ],
          '<%=config.dest%>/static/styles/all.css': [
            '<%=config.src_bower%>/bootstrap/dist/css/bootstrap.css',
            '<%=config.src%>/styles/*.css'
          ]
        }
      }
    },

    // js混淆压缩
    uglify: {
      options: {
        banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +
        '<%= grunt.template.today("yyyy-mm-dd") %> */',
        mangle: false //prevent changes to your variable and function names
      },
      release_yw: {
        files: [
          {
            expand: true,
            cwd: '<%=config.dest%>/static/scripts/',
            src: ['**/*.js', '!biz/*.js'],
            dest: '<%=config.dest%>/static/scripts/'
          }
        ]
      }
    },

    // css min
    cssmin: {
      release_yw: {
        files: [
          {
            expand: true,
            cwd: '<%=config.dest%>/static/styles/',
            src: '**/*.css',
            dest: '<%=config.dest%>/static/styles/'
          }
        ]
      }
    },
    // 添加指纹
    rev: {
      options: {
        algorithm: 'md5',
        length: 8
      },
      release_yw: {
        files: [{
          src: [
            '<%=config.dest%>/static/**/*.js',
            '<%=config.dest%>/static/**/*.css',
            '!<%=config.dest%>/static/scripts/biz/*.js'
          ]
        }]
      }
    },

    // clean all files in release
    clean: {
      release_yw: {
        src: [
          '<%=config.dest%>/static/**/*.js',
          '!<%=config.dest%>/static/scripts/biz/*.js',
          '<%=config.dest%>/static/**/*.css',
          '<%=config.dest%>/static/**/*.{jpeg,png,jpg,icon,gif,svg}',
          '<%=config.dest%>/views/**/*.html',
          '<%=config.dest%>/views/**/*.jsp',
          '!<%=config.dest%>/views/**/userManage.jsp',
          '!<%=config.dest%>/views/**/record.jsp',
          '!<%=config.dest%>/views/**/download.jsp'
        ]
      },
      clean_html: {
        src: [
          '<%=config.dest%>/views/**/index.html',
          '<%=config.dest%>/views/**/nav.html',
          '<%=config.dest%>/views/**/main.html'
        ]
      }
    },

      watch:{
          files: ['<%=config.src  %>/**/*'],
          tasks: ['debug']
      },
  usemin: { // 替换资源引用
      css: {
        files: {
          src: ['<%=config.dest%>/static/styles/*.css']
        }
      },
      js: ['<%=config.dest%>/static/scripts/*.js'],
      html: ['<%=config.dest%>/views/**/*.html', '<%=config.dest%>/views/**/*.jsp'],
      options: {
        filePrefixer:function(url){
          if(!url){
            return '';
          }
          return url.replace([ '../../'],'../');  //staging
         //return url.replace(['../', '../../'],'/'); //本地
        },
        patterns: {
          js: [
            [/(\.png)/, 'Replacing reference to *.png']
          ]
        },
        blockReplacements: {
          image: function (block) {
            return '<img src="' + block.dest + '" alt=""/>';
          }
        }
      }
    }
  });
  grunt.registerTask('debug', ['clean:release_yw', 'concat:release_yw', 'copy:release_yw', 'usemin', 'clean:clean_html']);
  grunt.registerTask('release_demo', ['clean:release_yw']);
  grunt.registerTask('release', ['clean:release_yw', 'concat:release_yw', 'uglify:release_yw', 'cssmin:release_yw', 'copy:release_yw', 'rev:release_yw', 'usemin', 'clean:clean_html']);
};
