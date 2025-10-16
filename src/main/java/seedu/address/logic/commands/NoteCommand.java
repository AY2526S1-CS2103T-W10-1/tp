package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Note;
import seedu.address.model.person.Parent;
import seedu.address.model.person.Person;
import seedu.address.model.person.Student;

/**
 * Changes the Note of an existing person in the address book.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the Note of the person identified "
            + "by the index number used in the last person listing. "
            + "All Note must be less than 100 characters. "
            + "Existing Note will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_NOTE + "[NOTE]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_NOTE + "Likes to swim.";

    public static final String MESSAGE_ADD_NOTE_SUCCESS = "Added Note to Person: %1$s";
    public static final String MESSAGE_DELETE_NOTE_SUCCESS = "Removed Note from Person: %1$s";

    private final Index index;
    private final Note note;

    /**
     * @param index of the person in the filtered person list to edit the Note
     * @param note of the person to be updated to
     */
    public NoteCommand(Index index, Note note) {
        requireAllNonNull(index, note);

        this.index = index;
        this.note = note;
    }
    @Override
    public CommandResult execute(Model model) throws CommandException {

        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson;
        if (personToEdit.getType().isStudent()) {
            Student studentToEdit = (Student) personToEdit;
            editedPerson = new Student(
                    studentToEdit.getName(),
                    studentToEdit.getPhone(),
                    studentToEdit.getEmail(),
                    studentToEdit.getAddress(),
                    note,
                    studentToEdit.getSchedule(),
                    studentToEdit.getCost(),
                    studentToEdit.getPaymentStatus(),
                    studentToEdit.getTags());
        } else {
            editedPerson = new Parent(
                    personToEdit.getName(),
                    personToEdit.getPhone(),
                    personToEdit.getEmail(),
                    personToEdit.getAddress(),
                    note,
                    personToEdit.getCost(),
                    personToEdit.getPaymentStatus(),
                    personToEdit.getTags()
            );
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether the Note is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = !note.value.isEmpty() ? MESSAGE_ADD_NOTE_SUCCESS : MESSAGE_DELETE_NOTE_SUCCESS;
        return String.format(message, personToEdit.getName());
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NoteCommand)) {
            return false;
        }

        // state check
        NoteCommand e = (NoteCommand) other;
        return index.equals(e.index)
                && note.equals(e.note);
    }
}
