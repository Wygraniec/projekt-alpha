import axios from "axios";
import {User} from "./userService.js";
import {Template} from "./templateService.js";

const API = `${import.meta.env.VITE_API_URL}/v1`;

export class Task {
    constructor(id, templateId, state, createdAt, createdBy, numberOfSubtasks) {
        this.id = id
        this.templateId = templateId
        this.state = state
        this.createdAt = createdAt
        this.createdBy = createdBy
        this.numberOfSubtasks = numberOfSubtasks
    }

    async getTemplate() {
        return await Template.findById(this.templateId)
    }

    static async findById(id) {
        let endpoint = `${API}/tasks/${id}`
        let data = (await axios.get(endpoint, User.fromLocalStorage().getAuthHeader())).data

        return new Task(
            data['id'],
            data['templateId'],
            data['state'],
            data['createdAt'],
            data['createdBy'],
            data['numberOfSubtasks']
        )
    }

    // TODO when backend is ready, check for existing tasks on the backend
    // TODO when backend is ready, get numberOfSubtasks from the backend
    /**
     * Returns id of a task, if there exists one (one being assigned to a specific user with a given template)
     * @param templateId id of the template, which user wants to solve
     * */
    static async createOrGet(templateId) {
        const response = await axios.post(
            `${API}/tasks`,
            {
                templateId: templateId,
                numberOfSubtasks: 3,
                userId: User.fromLocalStorage().id
            },
            User.fromLocalStorage().getAuthHeader()
        ).catch(e => { console.log(e) })

        return response.data['id']
    }

    /**
     * @returns Username of the person who assigned the task
     * */
    async getAssigningUsername() {
        const response = await axios.get(
            `${API}/users/${this.createdBy}`,
            User.fromLocalStorage().getAuthHeader()
        )

        return response.data['username']
    }

    async getFile() {
        let endpoint = `${API}/tasks/${this.id}/file`
        const response = await axios.get(endpoint, User.fromLocalStorage().getAuthHeader({responseType: 'text'}));

        return response.data;
    }

    async saveFile(fileContents) {
        let endpoint = `${API}/tasks/${this.id}/file`

        const blob = new Blob([fileContents], {type: 'text/plain'})
        const formData = new FormData();
        formData.append('file', blob, 'task.py')

        await axios.post(
            endpoint,
            formData,
            User.fromLocalStorage().getAuthHeader({
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }))
    }

    // TODO react to the result, so that the user knew their score on the given task/subtask

    /**
     * Sends the code for full validation to the backend
     * @param fileContents current version of code
     * */
    async check(fileContents) {
        await this.saveFile(fileContents)

        const result = await axios.post(
            `${API}/tasks/${this.id}/process`,
            {},
            User.fromLocalStorage().getAuthHeader()
        )

        console.log(result)
        return result
    }

    /**
     * Sends a specific subtask for a given kind of validation to the backend
     * @param fileContents current version of code
     * @param subtaskNumber number of subtask to be checked
     * @param processingType processing type - 'fast' or 'full'
     * */
    async checkSubtask(fileContents, subtaskNumber, processingType) {
        await this.saveFile(fileContents)

        const result = await axios.post(
            `${API}/tasks/${this.id}/subtasks/${subtaskNumber}/${processingType}process`,
            {},
            User.fromLocalStorage().getAuthHeader()
        )

        console.log(result)
        return result
    }
}

export class TaskPage {
    constructor(tasks, currentPage, totalPages, totalElements) {
        this.tasks = tasks
        this.currentPage = currentPage
        this.totalPages = totalPages
        this.totalElements = totalElements
    }
}

export const getTasks = async (page = 0, pageSize = 10, userId = 0, states = null) => {
    let endpoint = `${API}/users/${userId}/tasks/byState?page=${page}&size=${pageSize}`

    states.forEach(state => endpoint += `&taskStates=${state}`)


    let request = await axios.get(endpoint, User.fromLocalStorage().getAuthHeader())

    return new TaskPage(
        request.data['tasks'].map(data => new Task(data['id'], data['templateId'], data['state'], new Date(data['createdAt']), data['createdBy'], data['numberOfSubtasks'])),
        request.data['currentPage'],
        request.data['totalPages'],
        request.data['totalElements'],
        request.data['']
    )
}
