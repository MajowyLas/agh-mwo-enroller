package com.company.enroller.controllers;

import java.util.Collection;
import java.util.Map;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;



@RestController
@RequestMapping("/meetings")
public class MeetingsRestController {

    //    @Autowired
//    ParticipantService participantService;
    @Autowired
    MeetingService meetingService;
    @Autowired
    private ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findById(meeting.getId());
        if (foundMeeting != null) {
            return new ResponseEntity<>("Unable to create", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable long id) {
        Meeting foundMeeting = meetingService.findById(id);

        if (foundMeeting == null) {
            return new ResponseEntity<>("Unable to delete. Meetning doesn't exist", HttpStatus.NOT_FOUND);
        }
        meetingService.delete(foundMeeting);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {

        Meeting meeting = meetingService.findById(id);

        if (meeting == null) {
            return new ResponseEntity<>("Unable to update. Meeting doesn't exist", HttpStatus.NOT_FOUND);
        }

        meeting.setTitle(updatedMeeting.getTitle());
        meeting.setDescription(updatedMeeting.getDescription());
        meeting.setDate(updatedMeeting.getDate());

        meetingService.update(meeting);

        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }
// -->GET meetings/{id}/participants
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
          Meeting meeting = meetingService.findById(id);

        if (meeting == null) {
            return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting.getParticipants(), HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id, @RequestBody Participant entryParticipant) {


        Meeting foundMeeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(entryParticipant.getLogin());
        //login = participant.getLogin();

        if (foundMeeting == null) {
            return new ResponseEntity<>("Meeting doesn't exist", HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("Participant doesn't exist", HttpStatus.NOT_FOUND);
        }
        foundMeeting.addParticipant(participant);
        meetingService.update(foundMeeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- > DELETE meetings/{id}/participants/{login}

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long id, @PathVariable("login") String login) {
        Meeting foundMeeting = meetingService.findById(id);
        Participant participant = participantService.findByLogin(login);

        if (foundMeeting == null) {
            return new ResponseEntity<>("Meeting doesn't exist", HttpStatus.NOT_FOUND);
        }
        if (participant == null) {
            return new ResponseEntity<>("Participant doesn't exist", HttpStatus.NOT_FOUND);
        }

         foundMeeting.getParticipants().remove(participant);
         meetingService.update(foundMeeting);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}