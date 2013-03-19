package mazestormer.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mazestormer.robot.RobotSound;

public class PlaySoundCommand extends Command {

	private RobotSound sound;

	public PlaySoundCommand(CommandType type) {
		super(type);
	}

	public PlaySoundCommand(CommandType type, RobotSound sound) {
		this(type);
		setSound(sound);
	}

	public RobotSound getSound() {
		return sound;
	}

	public void setSound(RobotSound sound) {
		this.sound = sound;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		setSound(RobotSound.values()[dis.readInt()]);
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeInt(sound.ordinal());
	}

}
