
const format = require('format') as any

export default ({
    fetchLocalProjects: '/project',
    prepareProject: (scmUrl: string) =>
        format('/project/%s', encodeURIComponent(scmUrl)),
    getProjectStatus: (scmUrl: string) =>
        format('/project/%s/status', encodeURIComponent(scmUrl)),
    checkoutBranch: (scmUrl: string, branchName: string) =>
        format('/project/%s/branch/%s', encodeURIComponent(scmUrl), branchName),
    fetchSourceCode: (scmUrl: string, className: string) =>
        format('/project/%s/source/%s', encodeURIComponent(scmUrl), className)
})